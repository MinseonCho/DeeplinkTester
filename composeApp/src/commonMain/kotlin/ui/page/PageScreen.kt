package ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import model.QueryItem
import ui.NavRailItem
import ui.style.ColorConstant
import ui.utils.CustomDialog

@Composable
fun PageScreen(
    pageViewModel: PageViewModel,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    var selectedRailItem by remember { mutableIntStateOf(0) }

    val showAdbAbsolutePathDialog by pageViewModel.showADBAbsolutePathDialog.collectAsState()

    if (showAdbAbsolutePathDialog) {
        showADBAbsolutePathDialog(
            path = pageViewModel.adbAbsolutePath,
            onConfirmButtonClicked = pageViewModel::onAdbPathDialogConfirmButtonClicked,
            onDismissed = pageViewModel::onAdbPathDialogDismissed
        )
    }

    Row(
        modifier = Modifier
            .background(Color(0xFFF5F5F7))
            .fillMaxSize()
    ) {
        NavigationRail(
            containerColor = Color(0xFFF5F5F7),
            contentColor = Color(0xFFF5F5F7)
        ) {
            Spacer(Modifier.weight(1f))
            NavRailItem.entries.forEachIndexed { index, navRailItem ->
                NavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = navRailItem.iconRes,
                            contentDescription = navRailItem.description,
                            tint = Color(0xFF374957)
                        )
                    },
                    label = null,
                    selected = selectedRailItem == index,
                    onClick = {
                        selectedRailItem = index
                        pageViewModel.onNavRailIconClicked(navRailItem = navRailItem)
                    }
                )
            }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F7))
                    .padding(top = 30.dp, end = 15.dp)
            ) {

                UrlField(
                    url = pageViewModel.urlUiState,
                    onUrlChanged = pageViewModel::onUrlChanged,
                    onSendButtonClicked = pageViewModel::onSendButtonClicked
                )

                QueryContent(
                    queries = pageViewModel.queryList.toImmutableList(),
                    onKeyChanged = pageViewModel::onQueryKeyChanged,
                    onValueChanged = pageViewModel::onQueryValueChanged,
                    onCheckedChanged = pageViewModel::onCheckedChanged,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun QueryContent(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    onCheckedChanged: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = ColorConstant._E2EFFB,
                    shape = RoundedCornerShape(4.dp)
                ).padding(horizontal = 6.dp, vertical = 3.dp),
            text = "Query Params", // TODO: string resource 로 분리
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._5A8CDD,
        )

        Spacer(modifier = Modifier.height(10.dp))

        QueryTable(
            queries = queries,
            onKeyChanged = onKeyChanged,
            onValueChanged = onValueChanged,
            onCheckedChanged = onCheckedChanged
        )
    }
}

@Composable
fun QueryTable(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    onCheckedChanged: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = ColorConstant._E8E8E8),
        state = listState
    ) {
        item {
            QueryTableHeaderRow()
            Divider(color = ColorConstant._E8E8E8, modifier = Modifier.height(1.dp))
        }

        itemsIndexed(queries) { index, query ->
            SingleQuery(
                position = index,
                queryItem = query,
                onCheckedChanged = onCheckedChanged,
                onKeyChanged = onKeyChanged,
                onValueChanged = onValueChanged
            )
            if (queries.lastIndex > index) {
                Divider(color = ColorConstant._E8E8E8, modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
fun QueryTableHeaderRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Checkbox(
            modifier = Modifier.width(50.dp), // checkBox width 는 고정
            checked = false,
            enabled = false,
            onCheckedChange = {}
        )

        TableVerticalDivider()

        Text(
            text = "Key",
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .weight(weight = 0.3f, fill = true)
                .align(Alignment.CenterVertically),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._848484
        )

        TableVerticalDivider()

        Text(
            text = "Value",
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .weight(weight = 0.7f, fill = true)
                .align(Alignment.CenterVertically),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._848484
        )
    }
}

@Composable
fun SingleQuery(
    position: Int,
    queryItem: QueryItem,
    onCheckedChanged: (Int, Boolean) -> Unit,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Checkbox(
            modifier = Modifier.width(50.dp), // checkBox width 는 고정
            checked = queryItem.isChecked,
            colors = CheckboxDefaults.colors(
                checkedColor = ColorConstant._5A8CDD,
                checkmarkColor = Color.White,
                uncheckedColor = ColorConstant._E8E8E8
            ),
            onCheckedChange = { isChecked ->
                onCheckedChanged(position, isChecked)
            }
        )

        TableVerticalDivider()

        InputField(
            text = queryItem.key,
            onValueChanged = {
                onKeyChanged(position, it)
            },
            modifier = Modifier
                .padding(5.dp)
                .weight(weight = 0.3f, fill = true)
                .align(Alignment.CenterVertically)
        )

        TableVerticalDivider()

        InputField(
            text = queryItem.value,
            onValueChanged = {
                onValueChanged(position, it)
            },
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(weight = 0.7f, fill = true)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TableVerticalDivider() {
    Divider(
        color = ColorConstant._E8E8E8,
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isSingleLine: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = text,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = modifier
            .background(
                color = if (isFocused) ColorConstant._FAFAFA else Color.Transparent
            ).onFocusChanged {
                isFocused = it.isFocused
            },
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontSize = 12.sp,
            color = ColorConstant._848484,
            lineHeight = 16.sp,
            letterSpacing = 1.sp
        ),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = text,
            innerTextField = innerTextField,
            enabled = isEnabled,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            singleLine = isSingleLine,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color(0xfffaf7f7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
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
