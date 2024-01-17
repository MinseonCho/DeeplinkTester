package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import model.QueryItem

@Composable
fun SinglePage() {
    Column {
        UrlFieldWithSendButton(defaultUrl = "example://www.example.com") { url ->
            // TODO: call expect method
        }
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