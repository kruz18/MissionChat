package ru.kyamshanov.missionChat.container

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateStateImmediate
import pro.respawn.flowmvi.plugins.recover
import pro.respawn.flowmvi.plugins.reduce
import ru.kyamshanov.missionChat.contranct.WelcomeAction
import ru.kyamshanov.missionChat.contranct.WelcomeIntent
import ru.kyamshanov.missionChat.contranct.WelcomeState
//private typealias Ctx = PipelineContext<State, Intent, Action>

internal class WelcomeScreenContainer :
    Container<WelcomeState, WelcomeIntent, WelcomeAction> {

    override val store = store(initial = WelcomeState("Welcome to chat")) {
        configure {
            debuggable = true
            name = ""
        }
    }
}