package ru.kyamshanov.missionChat.container

import kotlinx.coroutines.awaitCancellation
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.plugins.whileSubscribed
import ru.kyamshanov.missionChat.contranct.MessagesAction
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.contranct.MessagesState
import ru.kyamshanov.missionChat.domain.ChatWindowInteractorFactory
import ru.kyamshanov.missionChat.domain.models.ChatWindowState
import ru.kyamshanov.missionChat.domain.models.Interlocutor
import ru.kyamshanov.missionChat.domain.models.MessageInference
import java.util.*

internal class MessagesContainer(
    chatWindowInteractorFactory: ChatWindowInteractorFactory
) : Container<MessagesState, MessagesIntent, MessagesAction> {

    private val chatWindowInteractor = chatWindowInteractorFactory.newInstance()

    override val store = store(initial = MessagesState.Loaded(emptyList())) {
        configure {
            debuggable = true
            name = "MessagesContainer"
        }

        whileSubscribed {
            try {
                awaitCancellation()
            } finally {
                chatWindowInteractor.release()
            }
        }

        recover {
            it.printStackTrace()
            updateState { MessagesState.Error(it) }
            null
        }

        reduce { intent ->
            when (intent) {
                is MessagesIntent.AddRequestMessage -> {
                    val interactorState = chatWindowInteractor.state.value
                    if (interactorState !is ChatWindowState.Idle) return@reduce

                    val userMessageId = UUID.randomUUID().toString()
                    val assistantMessageId = UUID.randomUUID().toString()
                    val timestamp = System.currentTimeMillis().toString()

                    updateStateImmediate<MessagesState.Loaded, _> {
                        copy(
                            messages = messages + listOf(
                                MessagesState.MessageModel(
                                    title = userMessageId,
                                    text = intent.message,
                                    date = timestamp,
                                    owner = MessagesState.MessageOwner.Human
                                ),
                                MessagesState.MessageModel(
                                    title = assistantMessageId,
                                    text = "Thinking...",
                                    date = timestamp,
                                    owner = MessagesState.MessageOwner.AI
                                )
                            )
                        )
                    }

                    val humanMessage = MessageInference.HumanMessage(
                        content = intent.message,
                        human = Interlocutor.Human(name = "User")
                    )

                    with(chatWindowInteractor) {
                        interactorState.submitMessage(humanMessage).collect { answeringState ->
                            updateState<MessagesState.Loaded, _> {
                                copy(
                                    messages = messages.map {
                                        if (it.title == assistantMessageId) {
                                            it.copy(text = answeringState.assistantMessage.content)
                                        } else it
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
