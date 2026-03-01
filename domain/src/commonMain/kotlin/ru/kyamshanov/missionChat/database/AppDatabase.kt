package ru.kyamshanov.missionChat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import ru.kyamshanov.missionChat.database.converters.UuidConverter
import ru.kyamshanov.missionChat.database.dao.MessageDao
import ru.kyamshanov.missionChat.database.entities.MessageEntity

@Database(entities = [MessageEntity::class], version = 1)
@TypeConverters(UuidConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
