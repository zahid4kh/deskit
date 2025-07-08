package deskit.dialogs.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ConfirmationDialogColors(
    val iconTint: Color,
    val confirmButtonColor: Color,
    val confirmButtonTextColor: Color,
    val cancelButtonColor: Color,
    val cancelButtonTextColor: Color
)

object ConfirmationDialogDefaults{
    @Composable
    fun colors(
        iconTint: Color = MaterialTheme.colorScheme.primary,
        confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
        confirmButtonTextColor: Color = MaterialTheme.colorScheme.onPrimary,
        cancelButtonColor: Color = MaterialTheme.colorScheme.errorContainer,
        cancelButtonTextColor: Color = MaterialTheme.colorScheme.onErrorContainer
    ): ConfirmationDialogColors = ConfirmationDialogColors(
        iconTint = iconTint,
        confirmButtonColor = confirmButtonColor,
        confirmButtonTextColor = confirmButtonTextColor,
        cancelButtonColor = cancelButtonColor,
        cancelButtonTextColor = cancelButtonTextColor
    )
}