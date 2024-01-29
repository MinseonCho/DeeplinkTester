package components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import model.QueryItem
import ui.page.PageViewModel

@Composable
fun PageScreen(
    viewModel: PageViewModel
) {
    val url by viewModel.url.collectAsState()

    Column {
        UrlFieldWithSendButton(
            url = url,
            onUrlChanged = viewModel::onUrlChanged,
            onSendButtonClicked = viewModel::onSendButtonClicked
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            style = TextStyle(
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            ),
            text = "Query Params"
        )
        HomeLazyColumn(
            queries = listOf(
                QueryItem(key = "deviceKey", value = "e-kejwndkfppqkk"),
                QueryItem(key = "productIndex", value = 1),
                QueryItem(key = "categoryIndex", value = 1002),
                QueryItem(key = "", value = "")
            )
        )
    }
}

@Composable
fun HomeLazyColumn(
    queries: List<QueryItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(queries) { query ->
            SingleQueryParam(
                key = query.key,
                value = query.value
            ) { isChecked ->
                println("mscho, queryName: ${query.key}, isChecked: $isChecked")
            }
        }
    }

    QueryAddButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onQueryAddButtonClicked = {}
    )
}

@Composable
fun QueryAddButton(
    onQueryAddButtonClicked: () -> Unit,
    modifier: Modifier
) {
    OutlinedButton(
        modifier = modifier,
        content = {
            Icons.Default.Add
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "ADD QUERY", color = Color(0xff8AB3FC))
        },
        onClick = {
            onQueryAddButtonClicked()
        }
    )
}

@Composable
fun SingleQueryParam(
    key: String,
    value: Any,
    onCheckBoxClicked: (Boolean) -> Unit
) {
    var checked by rememberSaveable { mutableStateOf(true) }

    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        ) {
            Checkbox(
                modifier = Modifier
                    .weight(weight = 10f, fill = true),
                checked = checked,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xffd8e6ff),
                    uncheckedColor = Color(0xffd8e6ff)
                ),
                onCheckedChange = { isChecked ->
                    onCheckBoxClicked(isChecked)
                    checked = isChecked
                }
            )

            QueryInputField(
                text = key,
                onValueChanged = {

                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(weight = 35f, fill = true)
                    .align(Alignment.CenterVertically)
            )

            QueryInputField(
                text = value.toString(),
                onValueChanged = {

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
    var inputText by remember { mutableStateOf(text) }
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = inputText,
        onValueChange = {
            inputText = it
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
            value = inputText,
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