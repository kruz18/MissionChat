package ru.kyamshanov.missionChat.domain.models


/**
 * Message comprising the conversation so far.
 */
sealed interface MessageInference {

    /**
     * @param content The contents of the system message.
     * @param associatedHuman An optional interlocutor for the participant. Provides the model information to differentiate between participants of the same role.
     */
    data class SystemMessage(
        val content: String,
        val associatedHuman: Interlocutor.Human? = null
    ) : MessageInference

    /**
     * @param content The contents of the user message.
     * @param human The human of the messages
     */
    data class HumanMessage(
        val content: String,
        val human: Interlocutor.Human,
    ) : MessageInference

    /**
     * @param content The contents of the user message.
     * @param associatedHuman An optional interlocutor for the participant. Provides the model information to differentiate between participants of the same role.
     *
     */
    data class AssistantMessage(
        val content: String,
        val associatedHuman: Interlocutor.Human? = null,
        val responseStartWith: String? = null
    ) : MessageInference


    /**
     * @param content The contents of the tool message.
     *
     *
     */
    data class ToolMessage(
        val content: String,
        val tool: Tool,
    ) : MessageInference
}