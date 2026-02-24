package ru.kyamshanov.missionChat

import com.arkivanov.decompose.ComponentContext
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.essenty.dsl.retainedStore
import ru.kyamshanov.missionChat.container.MessagesContainer
import ru.kyamshanov.missionChat.contranct.MessagesAction
import ru.kyamshanov.missionChat.contranct.MessagesIntent
import ru.kyamshanov.missionChat.contranct.MessagesState

interface MessagesComponent :
    Store<MessagesState, MessagesIntent, MessagesAction>


internal class DefaultMessagesComponent(
    componentContext: ComponentContext,
    containerFactory: () -> MessagesContainer,
) : MessagesComponent, ComponentContext by componentContext,
    Store<MessagesState, MessagesIntent, MessagesAction>
    by componentContext.retainedStore(factory = containerFactory)