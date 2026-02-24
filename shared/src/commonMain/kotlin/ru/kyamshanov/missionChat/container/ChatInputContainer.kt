package ru.kyamshanov.missionChat.container

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.reduce
import ru.kyamshanov.missionChat.contranct.ChatInputAction
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.contranct.ChatInputState

internal class ChatInputContainer(
    initial: ChatInputState
) : Container<ChatInputState, ChatInputIntent, ChatInputAction> {

    override val store = store(initial = initial) {
        configure {
            debuggable = true
            name = ""
        }

        reduce { intent ->
            when (intent) {
                is ChatInputIntent.ChangeInputValue -> {
                    updateStateImmediate<ChatInputState, _> {
                        copy(inputValue = intent.newValue)
                    }
                }

                is ChatInputIntent.ClickOnSendMessage -> {
                    withState {
                        println("Hello world!")
                        action(ChatInputAction.SendMessage(inputValue))
                    }
                }
            }
        }
    }
}