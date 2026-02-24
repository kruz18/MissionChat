package ru.kyamshanov.missionChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.models.MessagesStateUI
import ru.kyamshanov.missionChat.models.subscribeAsUiState
import ru.kyamshanov.missionChat.models.toUI

@Composable
fun WelcomeScreen(component: WelcomeScreenComponent, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        val modelState by component.subscribeAsUiState { it.toUI() }

        InitialWelcomeScreen(
            title = modelState.title,
            messagesComponent = { component.messagesComponent },
            chatInputComponent = { component.chatInputComponent }
        )
    }
}


@Composable
fun InitialWelcomeScreen(
    title: String,
    messagesComponent: () -> MessagesComponent,
    chatInputComponent: () -> ChatInputComponent,
) {
    Row(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        // --- 1. Боковая панель (Sidebar) ---
        Surface(
            modifier = Modifier.width(260.dp).fillMaxHeight(),
            color = Color.White,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Профиль
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("New Chat", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Now Speaking...", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Кнопки навигации
                SidebarItem(Icons.Default.Add, "Chats", selected = true)
                SidebarItem(Icons.Default.History, "History")
                SidebarItem(Icons.Default.Settings, "Settings")
            }
        }

        // --- 2. Основная область чата ---
        Column(modifier = Modifier.fillMaxSize()) {
            // Header с градиентом
            HeaderSection(title)

            // Список чатов/сообщений
            MessagesSection(messagesComponent)

            // Поле ввода (нижняя панель)
            InputSection(chatInputComponent)
        }
    }
}

@Composable
private fun ColumnScope.MessagesSection(
    messagesComponent: () -> MessagesComponent
) {
    val component = remember { messagesComponent() }
    val state by component.subscribeAsUiState { it.toUI() }

    when (val model = state) {
        is MessagesStateUI.Error -> TODO()
        is MessagesStateUI.Loaded -> {
            LoadedMessages(model.messages)
        }

        MessagesStateUI.Loading -> TODO()
    }

}

@Composable
private fun ColumnScope.LoadedMessages(
    messages: ImmutableList<MessagesStateUI.MessageModel>
) {
    Box(modifier = Modifier.weight(1f).padding(horizontal = 24.dp)) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.title }) { message ->
                ChatCard(
                    chat = ChatInfo(
                        title = message.title,
                        lastMessage = message.text,
                        date = message.date,
                        icon = Icons.Default.ChatBubbleOutline
                    )
                )
            }
        }
    }
}


@Composable
fun HeaderSection(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1A365D), Color(0xFF2B6CB0))
                )
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Face, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(12.dp))
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Row {
                IconButton(onClick = {}) { Icon(Icons.Default.Search, null, tint = Color.White) }
                IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = Color.White) }
            }
        }
    }
}

@Composable
fun ChatCard(chat: ChatInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF0052D4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(chat.icon, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(chat.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(chat.lastMessage, color = Color.Gray, fontSize = 14.sp)
                Text(chat.date, color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun InputSection(
    inputComponent: () -> ChatInputComponent,
) {
    val component = remember { inputComponent() }
    val state by component.subscribeAsUiState { it.toUI() }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = state.inputValue,
                onValueChange = { component.intent(ChatInputIntent.ChangeInputValue(it)) },
                placeholder = {
                    if (state.inputValue.isEmpty()) {
                        Text(state.typingHint)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
            IconButton(
                onClick = { component.intent(ChatInputIntent.ClickOnSendMessage) },
                enabled = state.inputValue.isNotBlank()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF2B6CB0))
            }
        }
    }
}

@Composable
fun SidebarItem(icon: ImageVector, label: String, selected: Boolean = false) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        color = if (selected) Color(0xFFE3F2FD) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Color(0xFF1976D2) else Color.DarkGray)
            Spacer(Modifier.width(12.dp))
            Text(label, color = if (selected) Color(0xFF1976D2) else Color.DarkGray, fontWeight = FontWeight.Medium)
        }
    }
}

data class ChatInfo(
    val title: String,
    val lastMessage: String,
    val date: String,
    val icon: ImageVector
)

/*
val chatData = listOf(
    ChatInfo("DeepSeek AI", "Hello! How can I assist you today?", "Yesterday", Icons.Default.AutoAwesome),
    ChatInfo("Project X", "Great progress on your code!", "Yesterday", Icons.Default.SmartToy),
    ChatInfo("New Idea", "Brainstorming session notes", "Oct 12", Icons.Default.Lightbulb)
)*/
