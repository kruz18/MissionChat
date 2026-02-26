package ru.kyamshanov.missionChat.container

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import ru.kyamshanov.missionChat.contranct.MessagesAction
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.contranct.MessagesState
import ru.kyamshanov.missionChat.network.DeepseekApi
import java.util.*

//private typealias Ctx = PipelineContext<State, Intent, Action>

internal class MessagesContainer(
    private val deepseekApi: DeepseekApi
) : Container<MessagesState, MessagesIntent, MessagesAction> {

    override val store = store(initial = MessagesState.Loaded(emptyList())) {
        configure {
            debuggable = true
            name = "MessagesContainer"
        }

        recover {
            it.printStackTrace()
            null
        }
        reduce { intent ->
            when (intent) {
                is MessagesIntent.AddRequestMessage -> {

                    val newMessageModel = MessagesState.MessageModel(
                        title = UUID.randomUUID().toString(),
                        text = "Thinking...",
                        date = "now"
                    )

                    updateStateImmediate<MessagesState.Loaded, _> {
                        copy(
                            messages = messages + MessagesState.MessageModel(
                                title = UUID.randomUUID().toString(),
                                text = intent.message,
                                date = System.currentTimeMillis().toString()
                            )
                        )
                    }

                    updateStateImmediate<MessagesState.Loaded, _> {
                        copy(messages = messages + newMessageModel)
                    }

                    deepseekApi.chatCompletionStream(
                        intent.message
                    ).collect { chatResponse ->
                        updateState<MessagesState.Loaded, _> {
                            copy(
                                messages = messages.map { messageModel ->
                                    if (messageModel.title == newMessageModel.title) {
                                        if (messageModel.text == newMessageModel.text) {
                                            messageModel.copy(text = chatResponse)
                                        } else {
                                            messageModel.copy(text = messageModel.text + chatResponse)
                                        }
                                    } else {
                                        messageModel
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}