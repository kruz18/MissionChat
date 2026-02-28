package ru.kyamshanov.missionChat.di

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ru.kyamshanov.missionChat.database.AppDatabase
import ru.kyamshanov.missionChat.database.createDatabase
import ru.kyamshanov.missionChat.domain.ChatWindowInteractorFactory
import ru.kyamshanov.missionChat.domain.impl.ChatWindowInteractorImpl
import ru.kyamshanov.missionChat.network.DeepseekApi
import ru.kyamshanov.missionChat.network.DeepseekApiImpl

val DomainDiModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        createHttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(message, tag = "HTTP Client")
                    }
                }
                level = LogLevel.HEADERS
            }
        }
    }

    single<DeepseekApi> { DeepseekApiImpl(get(), get()) }

    single<ChatWindowInteractorFactory> {
        ChatWindowInteractorFactory { ChatWindowInteractorImpl(get()) }
    }
}

val databaseModule = module {
    single { createDatabase(get()) }
    single { get<AppDatabase>().messageDao() }
}