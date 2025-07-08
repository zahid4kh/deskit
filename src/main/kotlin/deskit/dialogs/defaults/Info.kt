package deskit.dialogs.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class InfoDialogColors(
    val iconTint: Color,
    val okButtonColor: Color,
    val okButtonTextColor: Color
)

object InfoDialogDefaults{
    @Composable
    fun colors(
        iconTint: Color = MaterialTheme.colorScheme.primary,
        okButtonColor: Color = MaterialTheme.colorScheme.primary,
        okButtonTextColor: Color = MaterialTheme.colorScheme.onPrimary
    ): InfoDialogColors = InfoDialogColors(
        iconTint = iconTint,
        okButtonColor = okButtonColor,
        okButtonTextColor = okButtonTextColor
    )
}