package ru.kyamshanov.missionChat.container

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import ru.kyamshanov.missionChat.contranct.ChatInputAction
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.contranct.ChatInputState
import ru.kyamshanov.missionChat.utils.empty

internal class ChatInputContainer(
    initial: ChatInputState
) : Container<ChatInputState, ChatInputIntent, ChatInputAction> {

    override val store = store(initial = initial) {
        configure {
            debuggable = true
            name = "ChatInputContainer"
        }

        recover {
            it.printStackTrace()
            null
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
                        if (isGenerating) return@withState
                        val text = inputValue
                        updateStateImmediate<ChatInputState, _> {
                            copy(inputValue = String.empty, isGenerating = true)
                        }
                        action(ChatInputAction.SendMessage(text))
                    }
                }

                is ChatInputIntent.StopGeneration -> {
                    updateStateImmediate<ChatInputState, _> {
                        copy(isGenerating = false)
                    }
                    action(ChatInputAction.StopGeneration)
                }

                is ChatInputIntent.SetGenerating -> {
                    updateStateImmediate<ChatInputState, _> {
                        copy(isGenerating = intent.isGenerating)
                    }
                }
            }
        }
    }
}
