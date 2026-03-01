package ru.kyamshanov.missionChat.contranct

import kotlinx.datetime.LocalDateTime
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

sealed interface MessagesState : MVIState {
    data class Error(val e: Exception?) : MessagesState

    data object Loading : MessagesState

    data class Loaded(
        val messages: List<MessageModel>,
        val isGenerating: Boolean = false,
    ) : MessagesState

    data class MessageModel(
        val content: String,
        val owner: MessageOwner,
        val date: LocalDateTime,
        val name: String?,
        val id: String,
    )

    sealed interface MessageOwner {
        data object Human : MessageOwner

        data object AI : MessageOwner

        data object SYSTEM : MessageOwner
    }
}


sealed interface MessagesIntent : MVIIntent {

    data class AddRequestMessage(val message: String) : MessagesIntent

    data class DeleteMessage(val id: String) : MessagesIntent

    data object StopGeneration : MessagesIntent
}

sealed interface MessagesAction : MVIAction
