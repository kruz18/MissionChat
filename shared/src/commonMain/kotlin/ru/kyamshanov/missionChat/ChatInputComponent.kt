package ru.kyamshanov.missionChat

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.DelicateStoreApi
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.state
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import pro.respawn.flowmvi.essenty.dsl.subscribe
import ru.kyamshanov.missionChat.container.ChatInputContainer
import ru.kyamshanov.missionChat.contranct.ChatInputAction
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.contranct.ChatInputState
import ru.kyamshanov.missionChat.utils.retainedPersistedStore

interface ChatInputComponent :
    Store<ChatInputState, ChatInputIntent, ChatInputAction>


internal class DefaultChatInputComponent(
    componentContext: ComponentContext,
    containerFactory: (ChatInputState) -> ChatInputContainer,
    onSendMessage: (String) -> Unit,
) : ChatInputComponent, ComponentContext by componentContext,
    Store<ChatInputState, ChatInputIntent, ChatInputAction>
    by componentContext.retainedPersistedStore(
        initial = ChatInputState("Welcome to chating"),
        persistentKey = "ChatInput",
        serializer = ChatInputState.serializer(),
        builder = containerFactory
    ) {
   //by componentContext.retainedStore(factory = { containerFactory(ChatInputState("Welcome to chating"))  }  as () -> Container<ChatInputState, ChatInputIntent, ChatInputAction>) {
    init {
        subscribe {
            actions.collect {
                when (it) {
                    is ChatInputAction.SendMessage -> onSendMessage(it.text)
                }
            }
        }
    }

}