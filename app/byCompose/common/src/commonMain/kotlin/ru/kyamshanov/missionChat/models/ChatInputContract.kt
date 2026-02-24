package ru.kyamshanov.missionChat.models

import androidx.compose.runtime.Immutable
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import ru.kyamshanov.missionChat.contranct.ChatInputState
import ru.kyamshanov.missionChat.utils.empty

@Immutable
data class ChatInputStateUI(
    val typingHint: String,
    val inputValue: String = String.empty,
) : MVIState

fun ChatInputState.toUI(): ChatInputStateUI =
    ChatInputStateUI(
        typingHint = typingHint,
        inputValue = inputValue
    )
