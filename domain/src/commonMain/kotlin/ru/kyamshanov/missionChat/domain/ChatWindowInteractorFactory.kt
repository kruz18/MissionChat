package ru.kyamshanov.missionChat.domain

import ru.kyamshanov.missionChat.domain.models.Conversation

fun interface ChatWindowInteractorFactory {

    fun newInstance(conversation: Conversation): ChatWindowInteractor
}