package ru.kyamshanov.missionChat.domain.models

interface ChatWindowState {

    /**
     * Default initial state
     */
    data object Preparing : ChatWindowState

    /**
     * Chat ready to get messages from human
     */
    data class Idle(
        val messages: List<MessageInference>
    ) : ChatWindowState

    /**
     * After answering state will be changed to Idle
     */
    data class Answering(
        val assistantMessage: MessageInference.AssistantMessage
    ) : ChatWindowState

    /**
     * When chat window released
     */
    data object Closed : ChatWindowState
}