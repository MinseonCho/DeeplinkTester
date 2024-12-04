package ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val iconRes: ImageVector,
    val description: String
) {
    Page(Icons.Default.AddCircle, "Page"),
    Settings(Icons.Default.Settings, "Settings")
}
