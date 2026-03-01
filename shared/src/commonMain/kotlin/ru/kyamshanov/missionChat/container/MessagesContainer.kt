@file:OptIn(ExperimentalUuidApi::class)

package ru.kyamshanov.missionChat.container

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import ru.kyamshanov.missionChat.contranct.MessagesAction
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.contranct.MessagesState
import ru.kyamshanov.missionChat.domain.ChatWindowInteractor
import ru.kyamshanov.missionChat.domain.ChatWindowInteractorFactory
import ru.kyamshanov.missionChat.domain.models.ChatWindowState
import ru.kyamshanov.missionChat.domain.models.Conversation
import ru.kyamshanov.missionChat.domain.models.Interlocutor
import ru.kyamshanov.missionChat.domain.models.MessageInference
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class MessagesContainer(
    chatWindowInteractorFactory: ChatWindowInteractorFactory
) : Container<MessagesState, MessagesIntent, MessagesAction> {

    private val chatWindowInteractor: ChatWindowInteractor =
        chatWindowInteractorFactory.newInstance(Conversation())

    override val store = store(initial = MessagesState.Loading) {
        configure {
            debuggable = true
            name = "MessagesContainer"
        }

        whileSubscribed {
            chatWindowInteractor.state
                .onEach { interactorState ->
                    when (interactorState) {
                        is ChatWindowState.Idle -> {
                            updateState {
                                MessagesState.Loaded(
                                    messages = interactorState.messages.map { it.toModel() }
                                )
                            }
                        }

                        is ChatWindowState.Preparing -> {
                            updateState { MessagesState.Loading }
                        }

                        else -> Unit
                    }
                }
                .launchIn(this)

            try {
                awaitCancellation()
            } finally {
                chatWindowInteractor.release()
            }
        }

        recover {
            updateState { MessagesState.Error(it) }
            null
        }

        reduce { intent ->
            when (intent) {
                is MessagesIntent.AddRequestMessage -> {
                    val interactorState = chatWindowInteractor.state.value
                    if (interactorState !is ChatWindowState.Idle) return@reduce

                    val humanMessageId = Uuid.random()

                    val timestamp =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                    val humanMessage = MessageInference.HumanMessage(
                        content = intent.message,
                        human = Interlocutor.Human(name = "User"),
                        createdAt = timestamp,
                        id = humanMessageId
                    )
                    updateStateImmediate<MessagesState.Loaded, MessagesState> {
                        copy(
                            messages = messages + listOf(
                                MessagesState.MessageModel(
                                    content = intent.message,
                                    owner = MessagesState.MessageOwner.Human,
                                    date = timestamp,
                                    name = humanMessage.human.name,
                                    id = humanMessageId.toString()
                                ),
                            )
                        )
                    }

                    with(chatWindowInteractor) {
                        var assistantMessageId: String? = null
                        interactorState.submitMessage(humanMessage).collect { answeringState ->
                            updateStateImmediate<MessagesState.Loaded, MessagesState> {
                                val updatedMessaged =
                                    if (assistantMessageId == null) {
                                        assistantMessageId =
                                            answeringState.assistantMessage.id.toString()
                                        messages + answeringState.assistantMessage.toModel()
                                    } else {
                                        messages.map {
                                            if (it.id == answeringState.assistantMessage.id.toString()) {
                                                it.copy(content = answeringState.assistantMessage.content)
                                            } else it
                                        }
                                    }
                                copy(messages = updatedMessaged)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun MessageInference.toModel(): MessagesState.MessageModel {
    return when (this) {
        is MessageInference.AssistantMessage -> MessagesState.MessageModel(
            id = id.toString(),
            content = content,
            date = createdAt,
            owner = MessagesState.MessageOwner.AI,
            name = null,
        )

        is MessageInference.HumanMessage -> MessagesState.MessageModel(
            id = id.toString(),
            content = content,
            date = createdAt,
            owner = MessagesState.MessageOwner.Human,
            name = human.name,
        )

        is MessageInference.SystemMessage -> MessagesState.MessageModel(
            id = id.toString(),
            content = content,
            date = createdAt,
            owner = MessagesState.MessageOwner.AI,
            name = null,
        )

        is MessageInference.ToolMessage -> MessagesState.MessageModel(
            id = id.toString(),
            content = content,
            date = createdAt,
            owner = MessagesState.MessageOwner.AI,
            name = null,
        )
    }
}