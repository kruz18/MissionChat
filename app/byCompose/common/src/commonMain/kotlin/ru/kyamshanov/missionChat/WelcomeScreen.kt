package ru.kyamshanov.missionChat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import kotlinx.collections.immutable.ImmutableList
import ru.kyamshanov.missionChat.components.GlassBox
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.models.MessagesStateUI
import ru.kyamshanov.missionChat.models.MessagesStateUI.MessageModel.MessageType.*
import ru.kyamshanov.missionChat.models.subscribeAsUiState
import ru.kyamshanov.missionChat.models.toUI

@Composable
fun WelcomeScreen(component: WelcomeScreenComponent, modifier: Modifier = Modifier) {
    AppTheme {
        GlassBackground {
            val modelState by component.subscribeAsUiState { it.toUI() }

            InitialWelcomeScreen(
                title = modelState.title,
                messagesComponent = { component.messagesComponent },
                chatInputComponent = { component.chatInputComponent },
                modifier = modifier
            )
        }
    }
}

@Composable
fun InitialWelcomeScreen(
    title: String,
    messagesComponent: () -> MessagesComponent,
    chatInputComponent: () -> ChatInputComponent,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSystemInDarkTheme()) Color.White else Color(0xFF202124)
    val secondaryTextColor = textColor.copy(alpha = 0.7f)

    Row(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // --- 1. Левая панель ---
        GlassBox(
            modifier = Modifier.width(280.dp).fillMaxHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(textColor.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            Modifier.align(Alignment.Center),
                            tint = textColor
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "User Name",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = textColor
                        )
                        Text("Online", fontSize = 12.sp, color = secondaryTextColor)
                    }
                }
                Spacer(Modifier.height(32.dp))
                SidebarItem(
                    Icons.AutoMirrored.Filled.Chat,
                    "Chats",
                    selected = true,
                    textColor = textColor
                )
                SidebarItem(Icons.Default.History, "History", textColor = textColor)
                SidebarItem(Icons.Default.Settings, "Settings", textColor = textColor)
            }
        }

        Spacer(Modifier.width(16.dp))

        // --- 2. Основная область ---
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxHeight()
                    .align(Alignment.TopCenter)
            ) {
                GlassBox(
                    modifier = Modifier.fillMaxWidth().height(70.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    HeaderContent(title, textColor)
                }
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    MessagesSection(messagesComponent, textColor)
                }
                Spacer(Modifier.height(16.dp))
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    InputSectionContent(chatInputComponent, textColor)
                }
            }
        }
    }
}

@Composable
private fun HeaderContent(title: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = textColor)
            Spacer(Modifier.width(12.dp))
            Text(title, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        Row {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = textColor.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.MoreVert,
                    null,
                    tint = textColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun MessagesSection(messagesComponent: () -> MessagesComponent, textColor: Color) {
    val component = remember { messagesComponent() }
    val state by component.subscribeAsUiState { it.toUI() }
    when (val model = state) {
        is MessagesStateUI.Loaded -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
                reverseLayout = true,
            ) {
                items(model.messages.asReversed(), key = { it.id }) {
                    val icon: ImageVector
                    val iconDescription: String
                    when (it.messageType) {
                        Human -> {
                            icon = Icons.Default.Person
                            iconDescription = "Human"
                        }

                        AI_ASSISTANT -> {
                            icon = Icons.AutoMirrored.Filled.Chat
                            iconDescription = "AI Assistant"
                        }
                    }

                    ChatCard(
                        icon = icon,
                        iconContentDescription = iconDescription,
                        it.title,
                        it.text,
                        it.date,
                        textColor
                    )
                }
            }
        }

        else -> Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center),
                color = textColor
            )
        }
    }
}

@Composable
fun ChatCard(
    icon: ImageVector,
    iconContentDescription: String,
    title: String,
    lastMessage: String,
    date: String,
    textColor: Color
) {
    GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Bottom) {
            Box(
                Modifier.size(42.dp).clip(CircleShape).background(textColor.copy(alpha = 0.1f)),
                Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = textColor)
                    Text(date, color = textColor.copy(alpha = 0.6f), fontSize = 11.sp)
                }
                Markdown(lastMessage)
            }
        }
    }
}

@Composable
fun InputSectionContent(inputComponent: () -> ChatInputComponent, textColor: Color) {
    val component = remember { inputComponent() }
    val state by component.subscribeAsUiState { it.toUI() }
    Row(
        Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Add,
                null,
                tint = textColor.copy(alpha = 0.7f)
            )
        }
        TextField(
            value = state.inputValue,
            onValueChange = { component.intent(ChatInputIntent.ChangeInputValue(it)) },
            placeholder = { Text(state.typingHint, color = textColor.copy(alpha = 0.5f)) },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = textColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(
            onClick = { component.intent(ChatInputIntent.ClickOnSendMessage) },
            enabled = state.inputValue.isNotBlank(),
            modifier = Modifier.background(
                if (state.inputValue.isNotBlank()) textColor.copy(alpha = 0.15f) else Color.Transparent,
                CircleShape
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                null,
                tint = if (state.inputValue.isNotBlank()) textColor else textColor.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun SidebarItem(icon: ImageVector, label: String, selected: Boolean = false, textColor: Color) {
    Box(
        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(12.dp))
            .background(if (selected) textColor.copy(alpha = 0.12f) else Color.Transparent)
            .clickable {},
        contentAlignment = Alignment.CenterStart
    ) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                null,
                tint = if (selected) textColor else textColor.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                color = if (selected) textColor else textColor.copy(alpha = 0.6f),
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}
