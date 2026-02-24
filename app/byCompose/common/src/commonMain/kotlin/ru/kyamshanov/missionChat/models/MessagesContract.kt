package ru.kyamshanov.missionChat.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import ru.kyamshanov.missionChat.contranct.MessagesState

sealed interface MessagesStateUI {

    @Immutable
    data class Error(val e: ExceptionUI?) : MessagesStateUI

    @Immutable
    data object Loading : MessagesStateUI

    @Immutable
    data class Loaded(
        val messages: ImmutableList<MessageModel>,
    ) : MessagesStateUI

    @Immutable
    data class MessageModel(
        val title: String,
        val text: String,
        val date: String,
    )
}

fun MessagesState.toUI(): MessagesStateUI = when (this) {
    is MessagesState.Error -> MessagesStateUI.Error(e.toUI())
    is MessagesState.Loaded -> MessagesStateUI.Loaded(messages.map { it.toUI() }.toImmutableList())
    is MessagesState.Loading -> MessagesStateUI.Loading
}

fun MessagesState.MessageModel.toUI() = MessagesStateUI.MessageModel(title = title, text = text, date = date)