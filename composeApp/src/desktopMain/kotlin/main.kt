import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.NavigationItem
import ui.page.InputField
import ui.page.PageScreen
import ui.style.ColorConstant
import ui.utils.CustomDialog

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
        val viewModel = remember {
            MainViewModel()
        }
        var showAdbAbsolutePathDialog by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is MainEvent.ShowAdbPathDialog -> {
                        showAdbAbsolutePathDialog = true
                    }
                }
            }
        }

        MaterialTheme {
            var selectedRailItem by remember { mutableIntStateOf(0) }

            Row(
                modifier = Modifier
                    .background(Color(0xFFF5F5F7))
                    .fillMaxSize()
            ) {
                NavigationRail(
                    containerColor = Color(0xFFF5F5F7),
                    contentColor = Color(0xFFF5F5F7),
                    modifier = Modifier.width(50.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    NavigationItem.entries.forEachIndexed { index, navItem ->
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    imageVector = navItem.iconRes,
                                    contentDescription = navItem.description,
                                    tint = Color(0xFF374957),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = null,
                            selected = selectedRailItem == index,
                            onClick = {
                                selectedRailItem = index
                                viewModel.onNavItemClicked(navItem = navItem)
                            },
                        )
                    }
                }

                PageScreen(
                    onSendDeeplinkClicked = { url ->
                        coroutineScope.launch {
                            triggerUrl(
                                absoluteAdbPath = viewModel.adbAbsolutePath,
                                url = url
                            )
                        }
                    }
                )
            }
        }

        if (showAdbAbsolutePathDialog) {
            showADBAbsolutePathDialog(
                path = viewModel.adbAbsolutePath,
                onConfirmButtonClicked = viewModel::onAdbPathDialogConfirmButtonClicked,
                onDismissed = {
                    showAdbAbsolutePathDialog = false
                }
            )
        }
    }
}

suspend fun triggerUrl(
    absoluteAdbPath: String,
    url: String
) {
    withContext(Dispatchers.IO) {
        runCatching {
            // TODO: string resource 분리
            val command = "$absoluteAdbPath shell am start -a android.intent.action.VIEW -d $url"
            Runtime.getRuntime().exec(command)
        }.onFailure {
            // TODO: 에러 처리
        }
    }
}

@Composable
private fun showADBAbsolutePathDialog(
    path: String,
    onConfirmButtonClicked: (String) -> Unit,
    onDismissed: () -> Unit,
) {
    var pathString by remember { mutableStateOf(path) }
    val focusRequester = remember { FocusRequester() }

    CustomDialog(
        title = {
            Text(
                text = "ADB 절대 경로를 입력해주세요. 👀",
                fontWeight = FontWeight.Bold,
                color = ColorConstant._848484
            )
        },
        content = {
            InputField(
                text = pathString,
                onValueChanged = {
                    pathString = it
                },
                isSingleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        onDismissButtonClicked = onDismissed,
        onConfirmButtonClicked = {
            onConfirmButtonClicked(pathString)
        },
        onDismissed = onDismissed
    )

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

