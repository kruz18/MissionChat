package ru.kyamshanov.missionChat

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import ru.kyamshanov.missionChat.container.WelcomeScreenContainer
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.contranct.WelcomeAction
import ru.kyamshanov.missionChat.contranct.WelcomeIntent
import ru.kyamshanov.missionChat.contranct.WelcomeState
import ru.kyamshanov.missionChat.utils.ChatInputParams
import ru.kyamshanov.missionChat.utils.ComponentFactory
import ru.kyamshanov.missionChat.utils.MessagesParams

interface WelcomeScreenComponent :
    Store<WelcomeState, WelcomeIntent, WelcomeAction> {

    val messagesComponent: MessagesComponent

    val chatInputComponent: ChatInputComponent

}


internal class DefaultWelcomeScreenComponent(
    componentContext: ComponentContext,
    containerFactory: () -> WelcomeScreenContainer,
    componentFactory: ComponentFactory
) : WelcomeScreenComponent, ComponentContext by componentContext,
    Store<WelcomeState, WelcomeIntent, WelcomeAction>
    by componentContext.retainedStore(factory = containerFactory) {


    override val messagesComponent: MessagesComponent =
        componentFactory.createMessagesComponent(MessagesParams(childContext("messages")))

    override val chatInputComponent: ChatInputComponent =
        componentFactory.createChatInputComponent(
            ChatInputParams(
                componentContext = childContext("chatInput"),
                onSendMessage = { message ->
                    messagesComponent.intent(MessagesIntent.AddRequestMessage(message))
                },
                onStopGeneration = {
                    messagesComponent.intent(MessagesIntent.StopGeneration)
                }
            )
        )


}
