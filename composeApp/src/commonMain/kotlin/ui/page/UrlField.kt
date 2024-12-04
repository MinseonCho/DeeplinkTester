package ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.style.ColorConstant


@Composable
fun UrlField(
    url: String,
    onUrlChanged: (String) -> Unit,
    onSendButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
                .background(
                    color = ColorConstant._FEF7E1,
                    shape = RoundedCornerShape(4.dp)
                ).padding(horizontal = 6.dp, vertical = 3.dp),
            text = "Url",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._E6A358,
        )

        BasicTextField(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(10.dp)
                .weight(1f, fill = true),
            value = url,
            onValueChange = {
                onUrlChanged(it)
            },
            textStyle = TextStyle.Default.copy(
                color = ColorConstant._848484
            )
        )

        TextButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 3.dp),
            onClick = {
                onSendButtonClicked()
            },
            interactionSource = remember { MutableInteractionSource() },
            content = {
                Text(
                    text = "Send",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            },
            enabled = url.isNotBlank(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = ColorConstant._E6A358,
                disabledContentColor = ColorConstant._B4B4B4
            )
        )
    }
}
