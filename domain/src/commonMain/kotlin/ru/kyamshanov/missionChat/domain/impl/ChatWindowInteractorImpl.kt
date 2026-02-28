package ru.kyamshanov.missionChat.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import ru.kyamshanov.missionChat.domain.ChatWindowInteractor
import ru.kyamshanov.missionChat.domain.models.ChatWindowState
import ru.kyamshanov.missionChat.domain.models.MessageInference
import ru.kyamshanov.missionChat.models.Message
import ru.kyamshanov.missionChat.network.DeepseekApi

internal class ChatWindowInteractorImpl(
    private val api: DeepseekApi
) : ChatWindowInteractor {

    private val _state = MutableStateFlow<ChatWindowState>(ChatWindowState.Preparing)
    override val state: StateFlow<ChatWindowState> = _state.asStateFlow()
    private val chatHistory = mutableListOf<Message>()

    init {
        _state.update { ChatWindowState.Idle }
    }

    override fun ChatWindowState.Idle.submitMessage(
        message: MessageInference.HumanMessage
    ): Flow<ChatWindowState.Answering> = flow {
        var currentResponseContent = ""

        api.chatCompletionStream(
            userMessage = message.content,
            chatHistory = chatHistory.toList()
        )
            .onStart {
                _state.value = ChatWindowState.Answering(MessageInference.AssistantMessage(content = ""))
            }
            .onCompletion { error ->
                if (error == null) {
                    _state.value = ChatWindowState.Idle
                } else {
                    throw error
                }
            }
            .collect { chunk ->
                currentResponseContent += chunk

                val answeringState = ChatWindowState.Answering(
                    assistantMessage = MessageInference.AssistantMessage(content = currentResponseContent)
                )

                _state.value = answeringState
                emit(answeringState)
            }
    }

    override suspend fun release() {
        chatHistory.clear()
        _state.update { ChatWindowState.Closed }
    }
}