package ru.kyamshanov.missionChat.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val type: String, // SYSTEM, HUMAN, ASSISTANT, TOOL
    val content: String,
    val humanName: String? = null,
    val assistantAssociatedHumanName: String? = null,
    val responseStartWith: String? = null,
    val toolId: String? = null,
    val timestamp: Long
)
