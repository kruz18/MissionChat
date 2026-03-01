package ru.kyamshanov.missionChat.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.kyamshanov.missionChat.domain.models.ChatWindowState
import ru.kyamshanov.missionChat.domain.models.MessageInference

/**
 * Stateful
 *
 * Windows means window conversation - it is parts of the messages witch send to LLM
 *
 * This is stateful interface so be careful with state handle and don`t make singletone of this object.
 * Use only in factory
 *
 * This implement MVVM and state machine
 */
interface ChatWindowInteractor {

    /**
     * current state of chat window
     * Initial value is Preparing
     */
    val state: StateFlow<ChatWindowState>

    /**
     * Send message
     * available only then Idle state
     * return Resule.success if message sent success
     */
    fun ChatWindowState.Idle.submitMessage(message: MessageInference.HumanMessage): Flow<ChatWindowState.Answering>

    /**
     * delete message by id
     */
    suspend fun deleteMessage(messageId: String)

    /**
     * clear all data inside ChatWindowInteractor
     * Use for release memory only when this object will not be using
     */
    suspend fun release()
}