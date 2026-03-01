package ru.kyamshanov.missionChat.domain.models

data class Conversation(
    val chat: ChatInterface = ChatDefault,
)