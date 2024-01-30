package ui.page

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun UrlFieldWithSendButton(
    url: String,
    onUrlChanged: (String) -> Unit,
    onSendButtonClicked: () -> Unit
) {

    Row {
        OutlinedTextField(
            modifier = Modifier
                .padding(10.dp)
                .weight(90f, fill = true),
            value = url,
            onValueChange = {
                onUrlChanged(it)
            },
            label = { Text(text = "URL") }
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(10f, fill = true),
            onClick = {
                onSendButtonClicked()
            },
            content = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "open deeplink",
                    tint = Color(0xff8AB3FC)
                )
            },
            enabled = url.isNotBlank()
        )
    }
}