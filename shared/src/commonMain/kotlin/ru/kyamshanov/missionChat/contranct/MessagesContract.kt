package ru.kyamshanov.missionChat.contranct

import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

sealed interface MessagesState : MVIState {
    data class Error(val e: Exception?) : MessagesState

    data object Loading : MessagesState

    data class Loaded(
        val messages: List<MessageModel>,
    ) : MessagesState

    data class MessageModel(
        val title: String,
        val text: String,
        val date: String,
    )
}


sealed interface MessagesIntent : MVIIntent {

    data class AddRequestMessage(val message: String) : MessagesIntent
}

sealed interface MessagesAction : MVIAction

