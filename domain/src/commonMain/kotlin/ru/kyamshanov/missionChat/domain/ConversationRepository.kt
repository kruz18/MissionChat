package ru.kyamshanov.missionChat.domain

import ru.kyamshanov.missionChat.domain.models.Conversation
import ru.kyamshanov.missionChat.domain.models.MessageInference

internal interface ConversationRepository {

    suspend fun getMessages(conversation: Conversation): List<MessageInference>

    suspend fun insertMessage(conversation: Conversation, message: MessageInference): Long
}