package deskit.dialogs.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class FileSaverColors(
    val folderIconColor: Color,
    val fileIconColor: Color,
    val fileAndFolderListBG: Color,
    val folderNameColor: Color ,
    val fileNameColor: Color ,
    val infoIconTint: Color ,
    val scrollbarHoverColor: Color,
    val scrollbarUnhoverColor: Color,
    val tooltipColor: Color
)


object FileSaverDefaults{
    @Composable
    fun colors(
        folderIconColor: Color = MaterialTheme.colorScheme.primary,
        fileIconColor: Color = MaterialTheme.colorScheme.primary,
        fileAndFolderListBG: Color = MaterialTheme.colorScheme.secondaryContainer,
        folderNameColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        fileNameColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        infoIconTint: Color = MaterialTheme.colorScheme.secondary,
        scrollbarHoverColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        scrollbarUnhoverColor: Color = MaterialTheme.colorScheme.inversePrimary,
        tooltipColor: Color = MaterialTheme.colorScheme.tertiary
    ): FileSaverColors = FileSaverColors(
        folderIconColor = folderIconColor,
        fileIconColor = fileIconColor,
        fileAndFolderListBG = fileAndFolderListBG,
        folderNameColor = folderNameColor,
        fileNameColor = fileNameColor,
        infoIconTint = infoIconTint,
        scrollbarHoverColor = scrollbarHoverColor,
        scrollbarUnhoverColor = scrollbarUnhoverColor,
        tooltipColor = tooltipColor
    )
}