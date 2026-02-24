import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.jetbrains.compose.resources.painterResource
import missionchat.app.bycompose.desktop.generated.resources.Res
import missionchat.app.bycompose.desktop.generated.resources.app_icon
import ru.kyamshanov.missionChat.App
import ru.kyamshanov.missionChat.RootComponent
import ru.kyamshanov.missionChat.RootComponentFactory
import ru.kyamshanov.missionChat.initKoin


fun main() {
    val lifecycle = LifecycleRegistry()
    val root: RootComponent
    initKoin().also { koin ->
        // Always create the root component outside Compose on the UI thread
        root = runOnUiThread {
            koin.get<RootComponentFactory>()
                .create(DefaultComponentContext(lifecycle = lifecycle))
        }
    }


    application {
        val windowState = rememberWindowState()

        LifecycleController(lifecycle, windowState)

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "missionChat",
            icon = painterResource(Res.drawable.app_icon) //for generate Res class use `gradle :app:byCompose:desktop:generateComposeResClass`
        ) {
            App(root)
        }
    }
}