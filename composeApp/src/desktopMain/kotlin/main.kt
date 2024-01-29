import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.page.PageEvent
import ui.page.PageViewModel

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
        val viewModel = remember {
            PageViewModel()
        }

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is PageEvent.TriggerUrl -> {
                        triggerUrl(url = event.url)
                    }
                }
            }
        }

        App(
            pageViewModel = viewModel
        )
    }
}

suspend fun triggerUrl(url: String) {
    withContext(Dispatchers.IO) {
        runCatching {
            // TODO: string resource 분리
            val command = "adb shell am start -a android.intent.action.VIEW -d $url"
            Runtime.getRuntime().exec(command)
        }.onFailure {
            // TODO: 에러 처리
        }
    }
}
