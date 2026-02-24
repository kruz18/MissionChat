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
import ru.kyamshanov.missionChat.contranct.WelcomeAction
import ru.kyamshanov.missionChat.contranct.WelcomeIntent
import ru.kyamshanov.missionChat.contranct.WelcomeState

//private typealias Ctx = PipelineContext<State, Intent, Action>

internal class MessagesContainer :
    Container<MessagesState, MessagesIntent, MessagesAction> {

    override val store = store(initial = MessagesState.Loaded(emptyList())) {
        configure {
            debuggable = true
            name = "MessagesContainer"
        }
        reduce { intent ->
            when (intent) {
                is MessagesIntent.AddRequestMessage -> {
                    updateState<MessagesState.Loaded, _> {
                        copy(
                            messages = messages + MessagesState.MessageModel(
                                title = "msg",
                                text = intent.message,
                                date = "now"
                            )
                        )
                    }
                }
            }
        }
    }
}