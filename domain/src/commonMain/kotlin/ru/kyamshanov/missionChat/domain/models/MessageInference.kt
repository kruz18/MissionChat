@file:OptIn(ExperimentalUuidApi::class)

package ru.kyamshanov.missionChat.domain.models

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * Message comprising the conversation so far.
 */
sealed interface MessageInference {

    val id: Uuid
    val content: String
    val createdAt: LocalDateTime


    data class SystemMessage(
        override val id: Uuid,
        override val content: String,
        override val createdAt: LocalDateTime,
        val associatedHuman: Interlocutor.Human? = null
    ) : MessageInference

    /**
     * @param content The contents of the user message.
     * @param human The human of the messages
     */
    data class HumanMessage(
        override val id: Uuid,
        override val content: String,
        override val createdAt: LocalDateTime,
        val human: Interlocutor.Human,
    ) : MessageInference

    /**
     * @param content The contents of the user message.
     * @param associatedHuman An optional interlocutor for the participant. Provides the model information to differentiate between participants of the same role.
     *
     */
    data class AssistantMessage(
        override val id: Uuid,
        override val content: String,
        override val createdAt: LocalDateTime,
        val associatedHuman: Interlocutor.Human? = null,
        val responseStartWith: String? = null
    ) : MessageInference


    /**
     * @param content The contents of the tool message.
     *
     *
     */
    data class ToolMessage(
        override val id: Uuid,
        override val content: String,
        override val createdAt: LocalDateTime,
        val tool: Tool,
    ) : MessageInference
}