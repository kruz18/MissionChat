package ru.kyamshanov.missionChat.domain.models

sealed interface ChatInterface {

    /**
     * ID of the model to use. You can use deepseek-chat.
     */
    val name: String
}


data object ChatDefault : ChatInterface {
    override val name = "Chat"
}