package ru.kyamshanov.missionChat.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Entity(tableName = "messages")
@OptIn(ExperimentalUuidApi::class)
data class MessageEntity(
    @PrimaryKey val id: Uuid,
    val conversationId: String,
    val type: String, // SYSTEM, HUMAN, ASSISTANT, TOOL
    val content: String,
    val humanName: String? = null,
    val assistantAssociatedHumanName: String? = null,
    val responseStartWith: String? = null,
    val toolId: String? = null,
    val timestamp: Long
)
