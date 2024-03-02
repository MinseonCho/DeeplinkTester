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
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import model.QueryItem
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.NavRailItem
import ui.style.ColorConstant

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PageScreen(
    pageViewModel: PageViewModel
) {
    val url by pageViewModel.urlUiState.collectAsState()

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val snackBarHostState = remember { SnackbarHostState() }

    var selectedRailItem by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .background(Color(0xFFF5F5F7))
            .fillMaxSize()
    ) {
        NavigationRail(
            backgroundColor = Color(0xFFF5F5F7),
            contentColor = Color(0xFFF5F5F7)
        ) {
            Spacer(Modifier.weight(1f))
            NavRailItem.entries.forEachIndexed { index, navRailItem ->
                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(navRailItem.iconRes),
                            contentDescription = navRailItem.description,
                            tint = Color(0xFF374957)
                        )
                    },
                    label = null,
                    selected = selectedRailItem == index,
                    onClick = {
                        selectedRailItem = index
                        pageViewModel.onSettingsIconClicked()
                    }
                )
            }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F7))
                    .padding(top = 30.dp, end = 15.dp)
            ) {

                UrlField(
                    url = url,
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
    modifier: Modifier = Modifier
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

        QueryList(
            queries = queries,
            onKeyChanged = onKeyChanged,
            onValueChanged = onValueChanged,
            onCheckedChanged = onCheckedChanged
        )
    }
}

@Composable
fun QueryList(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    onCheckedChanged: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = ColorConstant._E8E8E8),
        state = listState
    ) {
        itemsIndexed(queries) { index, query ->
            SingleQueryParam(
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
fun SingleQueryParam(
    position: Int,
    queryItem: QueryItem,
    onCheckedChanged: (Int, Boolean) -> Unit,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Checkbox(
            modifier = Modifier
                .width(50.dp), // checkBox width 는 고정
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

        Divider(
            color = ColorConstant._E8E8E8,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        QueryInputField(
            text = queryItem.key,
            onValueChanged = {
                onKeyChanged(position, it)
            },
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(weight = 0.3f, fill = true)
                .align(Alignment.CenterVertically)
        )

        Divider(
            color = ColorConstant._E8E8E8,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        QueryInputField(
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QueryInputField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
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
        singleLine = true,
        textStyle = TextStyle(fontSize = TextUnit(12f, TextUnitType.Sp)),
    ) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = text,
            innerTextField = innerTextField,
            contentPadding = PaddingValues(10.dp),
            singleLine = true,
            enabled = true,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xfffaf7f7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
    }
}