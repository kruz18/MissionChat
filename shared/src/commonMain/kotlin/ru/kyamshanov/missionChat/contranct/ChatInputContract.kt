package ru.kyamshanov.missionChat.contranct

import kotlinx.serialization.Serializable
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import ru.kyamshanov.missionChat.utils.empty

@Serializable
data class ChatInputState(
    val typingHint: String,
    val inputValue: String = String.empty,
) : MVIState

sealed interface ChatInputIntent : MVIIntent {

    data class ChangeInputValue(
        val newValue: String
    ) : ChatInputIntent

    data object ClickOnSendMessage : ChatInputIntent
}

sealed interface ChatInputAction : MVIAction {

    data class SendMessage(val text: String) : ChatInputAction
}