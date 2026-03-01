package ru.kyamshanov.missionChat.database

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.kyamshanov.missionChat.database.dao.MessageDao
import ru.kyamshanov.missionChat.database.entities.MessageEntity
import ru.kyamshanov.missionChat.domain.ConversationRepository
import ru.kyamshanov.missionChat.domain.models.Conversation
import ru.kyamshanov.missionChat.domain.models.Interlocutor
import ru.kyamshanov.missionChat.domain.models.MessageInference
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class RoomConversationRepository(
    private val messageDao: MessageDao
) : ConversationRepository {

    override suspend fun getMessages(conversation: Conversation): List<MessageInference> =
        messageDao.getMessagesForConversation(conversation.chat.name).firstOrNull()
            ?.map { it.toDomain() }.orEmpty()

    override suspend fun insertMessage(
        conversation: Conversation,
        message: MessageInference
    ): Long =
        messageDao.insert(message.toEntity(conversation))

    override suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessage(messageId)
    }
}

@OptIn(ExperimentalTime::class)
private fun MessageEntity.toDomain(): MessageInference =
    when (type) {
        "SYSTEM" -> {
            MessageInference.SystemMessage(
                content = content,
                associatedHuman = assistantAssociatedHumanName?.let { Interlocutor.Human(it) },
                createdAt = Instant.fromEpochMilliseconds(timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                id = id,
            )
        }

        "HUMAN" -> {
            checkNotNull(humanName) { "Human name is null" }
            MessageInference.HumanMessage(
                content = content,
                human = Interlocutor.Human(humanName),
                createdAt = Instant.fromEpochMilliseconds(timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                id = id,
            )
        }

        "ASSISTANT" -> {
            MessageInference.AssistantMessage(
                content = content,
                associatedHuman = assistantAssociatedHumanName?.let { Interlocutor.Human(it) },
                responseStartWith = responseStartWith,
                createdAt = Instant.fromEpochMilliseconds(timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                id = id,
            )
        }

        "TOOL" -> {
            TODO("Tool is not supported yet")
        }

        else -> throw IllegalStateException("Unknown message type: ${type}")
    }


@OptIn(ExperimentalTime::class)
private fun MessageInference.toEntity(conversation: Conversation): MessageEntity =
    when (this) {
        is MessageInference.AssistantMessage -> {
            MessageEntity(
                conversationId = conversation.chat.name,
                type = "ASSISTANT",
                content = content,
                assistantAssociatedHumanName = associatedHuman?.name,
                responseStartWith = responseStartWith,
                timestamp = createdAt.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds(),
                humanName = null,
                toolId = null,
                id = id,
            )
        }

        is MessageInference.HumanMessage -> {
            MessageEntity(
                conversationId = conversation.chat.name,
                type = "HUMAN",
                content = content,
                humanName = human.name,
                assistantAssociatedHumanName = null,
                responseStartWith = null,
                timestamp = createdAt.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds(),
                id = id,
            )
        }

        is MessageInference.SystemMessage -> {
            MessageEntity(
                conversationId = conversation.chat.name,
                type = "SYSTEM",
                content = content,
                assistantAssociatedHumanName = associatedHuman?.name,
                responseStartWith = null,
                timestamp = createdAt.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds(),
                humanName = null,
                toolId = null,
                id = id,
            )
        }

        is MessageInference.ToolMessage -> {
            MessageEntity(
                conversationId = conversation.chat.name,
                type = "TOOL",
                content = content,
                assistantAssociatedHumanName = null,
                responseStartWith = null,
                timestamp = createdAt.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds(),
                humanName = null,
                toolId = tool.id,
                id = id,
            )
        }
    }
