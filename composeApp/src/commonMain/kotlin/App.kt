import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import components.PageScreen
import ui.page.PageViewModel

@Composable
fun App(
    pageViewModel: PageViewModel
) {
    MaterialTheme {
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        val snackBarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            scaffoldState = scaffoldState,
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) {
            PageScreen(
                viewModel = pageViewModel,
            )
        }
    }
}