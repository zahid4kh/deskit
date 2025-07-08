package deskit.dialogs.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class FileChooserColors(
    val folderIconColor: Color,
    val fileIconColor: Color,
    val fileAndFolderListBG: Color,
    val folderNameColor: Color ,
    val fileNameColor: Color ,
    val badgeColor: Color ,
    val badgeContentColor: Color,
    val infoIconTint: Color ,
    val scrollbarHoverColor: Color,
    val scrollbarUnhoverColor: Color,
    val tooltipColor: Color
)

object FileChooserDefaults{
    @Composable
    fun colors(
        folderIconColor: Color = MaterialTheme.colorScheme.primary,
        fileIconColor: Color = MaterialTheme.colorScheme.primary,
        fileAndFolderListBG: Color = MaterialTheme.colorScheme.secondaryContainer,
        folderNameColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        fileNameColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        badgeColor: Color = MaterialTheme.colorScheme.primary,
        badgeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        infoIconTint: Color = MaterialTheme.colorScheme.secondary,
        scrollbarHoverColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        scrollbarUnhoverColor: Color = MaterialTheme.colorScheme.inversePrimary,
        tooltipColor: Color = MaterialTheme.colorScheme.tertiary
    ): FileChooserColors = FileChooserColors(
        folderIconColor = folderIconColor,
        fileIconColor = fileIconColor,
        fileAndFolderListBG = fileAndFolderListBG,
        folderNameColor = folderNameColor,
        fileNameColor = fileNameColor,
        badgeColor = badgeColor,
        badgeContentColor = badgeContentColor,
        infoIconTint = infoIconTint,
        scrollbarHoverColor = scrollbarHoverColor,
        scrollbarUnhoverColor = scrollbarUnhoverColor,
        tooltipColor = tooltipColor
    )
}