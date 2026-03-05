package ru.kyamshanov.missionChat.welcomeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.kyamshanov.missionChat.AppTheme
import ru.kyamshanov.missionChat.ChatInputComponent
import ru.kyamshanov.missionChat.GlassBackground
import ru.kyamshanov.missionChat.MessagesComponent
import ru.kyamshanov.missionChat.components.WindowScaffold
import ru.kyamshanov.missionChat.components.WindowScaffoldRelative
import ru.kyamshanov.missionChat.components.WindowScaffoldRelative.SLIDEBAR
import ru.kyamshanov.missionChat.components.glassmorphism
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.models.MessagesStateUI
import ru.kyamshanov.missionChat.models.subscribeAsUiState
import ru.kyamshanov.missionChat.models.toUI

@Composable
fun WelcomeScreen(
    component: ru.kyamshanov.missionChat.WelcomeScreenComponent,
    modifier: Modifier = Modifier
) {
    AppTheme {
        GlassBackground {
            val modelState by component.subscribeAsUiState { it.toUI() }

            InitialWelcomeScreen(
                title = modelState.title,
                messagesComponent = component.messagesComponent,
                chatInputComponent = component.chatInputComponent,
                modifier = modifier
            )
        }
    }
}

@Composable
fun InitialWelcomeScreen(
    title: String,
    messagesComponent: MessagesComponent,
    chatInputComponent: ChatInputComponent,
    modifier: Modifier = Modifier
) {

    WindowScaffold(modifier = modifier.fillMaxSize()) {
        WelcomeSlidebar(
            modifier = Modifier.relative(SLIDEBAR)
        )

        Box(
            modifier = Modifier
                .relative(WindowScaffoldRelative.TOOLBAR)
                .height(70.dp)
                .glassmorphism(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                )
        ) {
            HeaderContent(title)
        }

        Column(
            modifier = Modifier
                .relative(WindowScaffoldRelative.CONTENT)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp)) {
                MessagesSection(messagesComponent)
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .glassmorphism(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                val mState by messagesComponent.subscribeAsUiState { it.toUI() }
                val isGenerating = (mState as? MessagesStateUI.Loaded)?.isGenerating == true

                InputSectionContent(chatInputComponent, isGenerating)
            }
        }
    }
}

@Composable
fun MessagesSection(
    component: MessagesComponent,
) {
    val state by component.subscribeAsUiState { it.toUI() }
    when (val model = state) {
        is MessagesStateUI.Loaded -> {
            MessagesList(
                messages = model.messages,
                onDelete = { component.intent(MessagesIntent.DeleteMessage(it)) })
        }

        else -> Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}