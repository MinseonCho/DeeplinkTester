package ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import model.QueryItem

@Composable
fun PageScreen(
    pageViewModel: PageViewModel
) {
    val url by pageViewModel.urlUiState.collectAsState()

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column {
            UrlFieldWithSendButton(
                url = url,
                onUrlChanged = pageViewModel::onUrlChanged,
                onSendButtonClicked = pageViewModel::onSendButtonClicked
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                style = TextStyle(
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                ),
                text = "Query Params" // TODO: string resource 로 분리
            )

            QueryContent(
                queries = pageViewModel.queryList.toImmutableList(),
                onKeyChanged = pageViewModel::onQueryKeyChanged,
                onValueChanged = pageViewModel::onQueryValueChanged,
                onCheckedChanged = pageViewModel::onCheckedChanged
            )
        }
    }
}

@Composable
fun QueryContent(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    onCheckedChanged: (Int, Boolean) -> Unit
) {
    QueryList(
        queries = queries,
        onKeyChanged = onKeyChanged,
        onValueChanged = onValueChanged,
        onCheckedChanged = onCheckedChanged
    )
}

@Composable
fun QueryList(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit,
    onCheckedChanged: (Int, Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
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
        }
    }
}

@Composable
fun SingleQueryParam(
    position: Int,
    queryItem: QueryItem,
    onCheckedChanged: (Int, Boolean) -> Unit,
    onKeyChanged: (Int, String) -> Unit,
    onValueChanged: (Int, String) -> Unit
) {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        ) {
            Checkbox(
                modifier = Modifier
                    .weight(weight = 10f, fill = true),
                checked = queryItem.isChecked,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xffd8e6ff),
                    uncheckedColor = Color(0xffd8e6ff)
                ),
                onCheckedChange = { isChecked ->
                    onCheckedChanged(position, isChecked)
                }
            )

            QueryInputField(
                text = queryItem.key,
                onValueChanged = {
                    onKeyChanged(position, it)
                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(weight = 35f, fill = true)
                    .align(Alignment.CenterVertically)
            )

            QueryInputField(
                text = queryItem.value,
                onValueChanged = {
                    onValueChanged(position, it)
                },
                modifier = Modifier
                    .weight(weight = 55f, fill = true)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QueryInputField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = text,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = modifier.then(
            Modifier.background(
                shape = RoundedCornerShape(3.dp),
                color = Color(0xfffaf7f7)
            )
        ),
        singleLine = true,
        textStyle = TextStyle(fontSize = TextUnit(11f, TextUnitType.Sp)),
    ) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = text,
            innerTextField = innerTextField,
            contentPadding = PaddingValues(5.dp),
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