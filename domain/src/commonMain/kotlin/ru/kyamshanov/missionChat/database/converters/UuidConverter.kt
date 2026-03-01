package ru.kyamshanov.missionChat.database.converters

import androidx.room.TypeConverter
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UuidConverter {
    @TypeConverter
    fun fromString(value: String?): Uuid? {
        return value?.let { Uuid.parse(it) }
    }

    @TypeConverter
    fun toString(uuid: Uuid?): String? {
        return uuid?.toString()
    }
}
