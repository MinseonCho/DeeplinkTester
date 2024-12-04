import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.collections.immutable.toImmutableList
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
        val sendLogMaxSize = 30
        val sendLogTexts = remember { mutableStateListOf<String>() }
        val coroutineScope = rememberCoroutineScope()
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    MainEvent.ShowPage -> {
                        if (navController.currentDestination?.route != NavDestination.Page.route) {
                            navController.navigate(
                                route = NavDestination.Page.route
                            )
                        }
                    }

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
                            selected = false,
                            onClick = {
                                selectedRailItem = index
                                viewModel.onNavItemClicked(navItem = navItem)
                            },
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavDestination.Page.route
                    ) {
                        composable(route = NavDestination.Page.route) {
                            PageScreen(
                                onSendDeeplinkClicked = { url ->
                                    coroutineScope.launch {
                                        triggerUrl(
                                            absoluteAdbPath = viewModel.adbAbsolutePath,
                                            url = url,
                                            onError = { errorMsg ->
                                                sendLogTexts.add(errorMsg)
                                                if (sendLogTexts.size > sendLogMaxSize) {
                                                    sendLogTexts.removeFirstOrNull()
                                                }
                                            }
                                        )
                                    }
                                },
                                sendLogTexts = sendLogTexts.toImmutableList()
                            )
                        }

                        composable(route = NavDestination.History.route) {
                        }
                    }
                }
            }

            if (showAdbAbsolutePathDialog) {
                showADBAbsolutePathDialog(
                    path = viewModel.adbAbsolutePath,
                    onConfirmButtonClicked = viewModel::onAdbPathDialogConfirmButtonClicked,
                    onDismissed = {
                        showAdbAbsolutePathDialog = false
                    },
                )
            }
        }
    }
}

suspend fun triggerUrl(
    absoluteAdbPath: String,
    url: String,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        runCatching {
            val command = "$absoluteAdbPath shell am start -a android.intent.action.VIEW -d \"$url\""
            val process = Runtime.getRuntime().exec(command)
            
            val errorStream = process.errorStream.bufferedReader().use { it.readText() }
            if (errorStream.isNotEmpty()) {
                throw Exception(errorStream)
            }
            
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw Exception("Command failed with exit code: $exitCode")
            }
        }.onFailure { throwable ->
            withContext(Dispatchers.Main) {
                onError(throwable.message ?: "알 수 없는 오류가 발생했습니다.")
            }
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

