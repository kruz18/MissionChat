package ru.kyamshanov.missionChat.domain.models

/**
 * a participant in a dialogue or conversation
 */
interface Interlocutor {

    /**
     * The person typing the prompts is the primary interlocutor.
     */
    data class Human(
        val name: String
    ) : Interlocutor

    /**
     * The LLM itself is the second interlocutor
     */
    data object Assistant : Interlocutor
}