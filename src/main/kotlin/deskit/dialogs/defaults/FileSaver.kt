package deskit.dialogs.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Represents the color scheme for FileSaverDialog components.
 *
 * @param folderIconColor Color applied to folder icons
 * @param fileIconColor Color applied to file icons
 * @param fileAndFolderListBG Background color of the file/folder list area
 * @param folderNameColor Text color for folder names
 * @param fileNameColor Text color for file names (dimmed since not selectable)
 * @param infoIconTint Color for info icons that appear on hover
 * @param scrollbarHoverColor Scrollbar color when hovered
 * @param scrollbarUnhoverColor Scrollbar color when not hovered
 */
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

/**
 * Contains default values for FileSaverDialog colors.
 */
object FileSaverDefaults{
    /**
     * Creates a [FileSaverColors] instance with the provided color values.
     * Any unspecified colors will use the default Material3 theme colors.
     */
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