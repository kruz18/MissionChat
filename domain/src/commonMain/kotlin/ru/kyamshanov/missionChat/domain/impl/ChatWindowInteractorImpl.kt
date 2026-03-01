@file:OptIn(ExperimentalUuidApi::class)

package ru.kyamshanov.missionChat.domain.impl

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.kyamshanov.missionChat.domain.ChatWindowInteractor
import ru.kyamshanov.missionChat.domain.ConversationRepository
import ru.kyamshanov.missionChat.domain.models.ChatWindowState
import ru.kyamshanov.missionChat.domain.models.Conversation
import ru.kyamshanov.missionChat.domain.models.MessageInference
import ru.kyamshanov.missionChat.models.Message
import ru.kyamshanov.missionChat.network.DeepseekApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Интерктор для работы с чат-окном.
 */
internal class ChatWindowInteractorImpl(
    private val conversation: Conversation,
    private val api: DeepseekApi,
    private val conversationRepository: ConversationRepository,
) : ChatWindowInteractor {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _state = MutableStateFlow<ChatWindowState>(ChatWindowState.Preparing)
    override val state: StateFlow<ChatWindowState> = _state.asStateFlow()

    private val messageInferences = mutableListOf<MessageInference>()
    private val chatHistory = mutableListOf<Message>()

    init {
        scope.launch {
            val loadedMessages = conversationRepository.getMessages(conversation)
            messageInferences.addAll(loadedMessages)
            chatHistory.addAll(loadedMessages.map { Message(it.role, it.content) })
            _state.update { ChatWindowState.Idle(messages = messageInferences.toList()) }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun ChatWindowState.Idle.submitMessage(
        message: MessageInference.HumanMessage
    ): Flow<ChatWindowState.Answering> = flow {
        scope.launch {
            conversationRepository.insertMessage(conversation, message)
        }
        messageInferences.add(message)
        chatHistory.add(Message(message.role, message.content))

        val assistantMessageId = Uuid.random()
        var currentResponseContent = ""


        api.chatCompletionStream(
            userMessage = message.content,
            chatHistory = chatHistory.dropLast(1) // История до текущего сообщения
        )
            .onStart {
                _state.value = ChatWindowState.Answering(
                    MessageInference.AssistantMessage(
                        content = "",
                        createdAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                        associatedHuman = message.human,
                        id = assistantMessageId,
                    )
                )
            }
            .onCompletion { error ->
                if (error == null || error is CancellationException) {
                    val assistantMessage =
                        MessageInference.AssistantMessage(
                            content = currentResponseContent,
                            createdAt = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()),
                            associatedHuman = message.human,
                            id = assistantMessageId,
                        )

                    scope.launch {
                        conversationRepository.insertMessage(conversation, assistantMessage)
                    }
                    messageInferences.add(assistantMessage)
                    chatHistory.add(Message(assistantMessage.role, assistantMessage.content))

                    _state.value = ChatWindowState.Idle(messages = messageInferences.toList())
                } else {
                    _state.value = ChatWindowState.Idle(messages = messageInferences.toList())
                }
            }
            .collect { chunk ->
                currentResponseContent += chunk

                val answeringState = ChatWindowState.Answering(
                    assistantMessage = MessageInference.AssistantMessage(
                        content = currentResponseContent,
                        createdAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                        associatedHuman = message.human,
                        id = assistantMessageId,
                    )
                )

                _state.value = answeringState
                emit(answeringState)
            }
    }

    override suspend fun deleteMessage(messageId: String) {
        conversationRepository.deleteMessage(messageId)
        messageInferences.removeAll { it.id.toString() == messageId }
        chatHistory.clear()
        chatHistory.addAll(messageInferences.map { Message(it.role, it.content) })
        _state.update { ChatWindowState.Idle(messages = messageInferences.toList()) }
    }

    override suspend fun release() {
        scope.cancel("ChatWindowInteractor is releasing")
        messageInferences.clear()
        chatHistory.clear()
        _state.update { ChatWindowState.Closed }
    }
}

private val MessageInference.role: String
    get() = when (this) {
        is MessageInference.AssistantMessage -> "assistant"
        is MessageInference.HumanMessage -> "user"
        is MessageInference.SystemMessage -> "system"
        is MessageInference.ToolMessage -> "tool"
    }

private val MessageInference.content: String
    get() = when (this) {
        is MessageInference.AssistantMessage -> content
        is MessageInference.HumanMessage -> content
        is MessageInference.SystemMessage -> content
        is MessageInference.ToolMessage -> content
    }
