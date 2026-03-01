package ru.kyamshanov.missionChat

import pro.respawn.flowmvi.api.Store
import ru.kyamshanov.missionChat.contranct.ChatInputAction
import ru.kyamshanov.missionChat.contranct.ChatInputIntent
import ru.kyamshanov.missionChat.contranct.ChatInputState

interface ChatInputComponent :
    Store<ChatInputState, ChatInputIntent, ChatInputAction>