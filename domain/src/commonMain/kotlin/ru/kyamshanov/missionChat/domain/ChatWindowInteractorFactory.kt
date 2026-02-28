package ru.kyamshanov.missionChat.domain

fun interface ChatWindowInteractorFactory {

    fun newInstance(): ChatWindowInteractor
}