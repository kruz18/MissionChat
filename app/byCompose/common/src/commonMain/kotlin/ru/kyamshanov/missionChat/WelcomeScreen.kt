package ru.kyamshanov.missionChat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import ru.kyamshanov.missionChat.components.GlassBox
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.models.MessagesStateUI
import ru.kyamshanov.missionChat.models.MessagesStateUI.MessageModel.MessageType.AI_ASSISTANT
import ru.kyamshanov.missionChat.models.MessagesStateUI.MessageModel.MessageType.Human
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
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp)) {
                    MessagesSection(messagesComponent, textColor)
                }
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    val mComponent = remember { messagesComponent() }
                    val mState by mComponent.subscribeAsUiState { it.toUI() }
                    val isGenerating = (mState as? MessagesStateUI.Loaded)?.isGenerating == true

                    InputSectionContent(chatInputComponent, isGenerating, textColor)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessagesSection(messagesComponent: () -> MessagesComponent, textColor: Color) {
    val component = remember { messagesComponent() }
    val state by component.subscribeAsUiState { it.toUI() }
    when (val model = state) {
        is MessagesStateUI.Loaded -> {
            val listState = rememberLazyListState()
            LaunchedEffect(model.messages.size) {
                if (listState.firstVisibleItemIndex <= 1) {
                    listState.animateScrollToItem(0)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
                reverseLayout = true,
            ) {
                items(model.messages.asReversed(), key = { it.id }) {
                    val icon: ImageVector
                    val iconDescription: String
                    val backgroundColor: Color
                    when (it.messageType) {
                        Human -> {
                            icon = Icons.Default.Person
                            iconDescription = "Human"
                            backgroundColor = Color.White
                        }

                        AI_ASSISTANT -> {
                            icon = Icons.AutoMirrored.Filled.Chat
                            iconDescription = "AI Assistant"
                            backgroundColor = Color.Gray
                        }

                        MessagesStateUI.MessageModel.MessageType.SYSTEM -> TODO()
                    }

                    ChatCard(
                        modifier = Modifier.animateItem(),
                        icon = icon,
                        iconContentDescription = iconDescription,
                        title = it.name,
                        lastMessage = it.content,
                        textColor = textColor,
                        backgroundColor = backgroundColor,
                        onDelete = { component.intent(MessagesIntent.DeleteMessage(it.id)) }
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatCard(
    icon: ImageVector,
    iconContentDescription: String,
    title: String?,
    lastMessage: String,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {}
) {
    var isHovered by remember { mutableStateOf(false) }

    GlassBox(
        modifier = modifier
            .fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = backgroundColor,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
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
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    if (!title.isNullOrBlank()) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {

                            Text(
                                text = title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = textColor
                            )
                        }
//                    Text(date, color = textColor.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                    SelectionContainer {
                        Markdown(lastMessage)
                    }
                }
            }

            AnimatedVisibility(
                visible = isHovered,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InputSectionContent(
    inputComponent: () -> ChatInputComponent,
    isGenerating: Boolean,
    textColor: Color
) {
    val component = remember { inputComponent() }
    val state by component.subscribeAsUiState { it.toUI() }

    LaunchedEffect(isGenerating) {
        component.intent(ChatInputIntent.SetGenerating(isGenerating))
    }
    
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
            modifier = Modifier
                .weight(1f)
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        if (it.isShiftPressed) {
                            false
                        } else {
                            if (isGenerating) {
                                component.intent(ChatInputIntent.StopGeneration)
                            } else if (state.inputValue.isNotBlank()) {
                                component.intent(ChatInputIntent.ClickOnSendMessage)
                            }
                            true
                        }
                    } else {
                        false
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = textColor
            )
        )
        IconButton(
            onClick = {
                if (isGenerating) {
                    component.intent(ChatInputIntent.StopGeneration)
                } else if (state.inputValue.isNotBlank()) {
                    component.intent(ChatInputIntent.ClickOnSendMessage)
                }
            },
            enabled = state.inputValue.isNotBlank() || isGenerating
        ) {
            Icon(
                if (isGenerating) Icons.Default.Stop else Icons.AutoMirrored.Filled.Send,
                null,
                tint = textColor
            )
        }
    }
}

@Composable
fun SidebarItem(icon: ImageVector, text: String, selected: Boolean = false, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) textColor.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(20.dp),
            tint = if (selected) textColor else textColor.copy(alpha = 0.7f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = if (selected) textColor else textColor.copy(alpha = 0.7f)
        )
    }
}
