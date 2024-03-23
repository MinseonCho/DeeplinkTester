package ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ui.style.ColorConstant

@Composable
fun CustomDialog(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmButtonClicked: (() -> Unit)? = null,
    onDismissButtonClicked: (() -> Unit)? = null,
    dismissButtonText: String = "닫기",
    confirmButtonText: String = "확인",
) {

    Dialog(
        onDismissRequest = {
            onDismissed()
        }, properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                title.invoke()
                Spacer(Modifier.size(16.dp))
                content.invoke()
            }

            Spacer(modifier = Modifier.size(4.dp))

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                DialogButton(
                    onClicked = {
                        onDismissed()
                        onDismissButtonClicked?.invoke()
                    },
                    text = dismissButtonText
                )

                DialogButton(
                    onClicked = {
                        onDismissed()
                        onConfirmButtonClicked?.invoke()
                    },
                    text = confirmButtonText
                )
            }
        }
    }
}

@Composable
private fun DialogButton(
    onClicked: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClicked,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._E6A358
        )
    }
}