package dialogs.file

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import java.awt.Dimension
import java.io.File

/**
 * Displays a folder selection dialog with directory navigation and breadcrumb trail.
 *
 * This dialog allows users to browse and select folders from the file system. It shows
 * only directories, hiding files and system folders (those starting with ".").
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Choose Folder".
 * @param startDirectory The initial directory to display when the dialog opens.
 *                       Defaults to the user's Downloads folder.
 * @param onFolderSelected Callback function invoked with the selected File (directory) when
 *                         the user clicks Choose.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * @sample dialogs.file.FolderChooserDialogSample
 */
@Composable
fun FolderChooserDialog(
    title: String = "Choose Folder",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    onFolderSelected: (File) -> Unit,
    onCancel: () -> Unit
) {
    var currentDir by remember { mutableStateOf(startDirectory) }
    val folders = remember(currentDir) {
        currentDir.listFiles()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.sortedBy { it.name } ?: emptyList()
    }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

    val dialogState = rememberDialogState(size = DpSize(600.dp, 600.dp), position = WindowPosition(Alignment.Center))

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onCancel
    ) {
        window.minimumSize = Dimension(600, 600)
        Surface {
            Column(Modifier.padding(16.dp)) {
                Text("Select a Folder", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp)
                ) {
                    pathSegments.forEachIndexed { index, dir ->
                        Text(
                            text = dir.name.ifBlank { "Home" },
                            color = if (index == pathSegments.lastIndex)
                                MaterialTheme.colorScheme.primary else LocalContentColor.current,
                            modifier = Modifier.clickable {
                                currentDir = dir
                            },
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        if (index != pathSegments.lastIndex) {
                            Text("/", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                LazyColumn(Modifier.weight(1f)) {
                    items(folders) { folder ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    currentDir = folder
                                }
                                .padding(9.dp)
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(folder.name)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onFolderSelected(currentDir) }) {
                        Text("Choose")
                    }
                }
            }
        }
    }
}


/**
 * A sample composable function demonstrating the usage of the [FolderChooserDialog].
 *
 * This sample displays a button that, when clicked, shows a [FolderChooserDialog].
 * The dialog is configured with the title "Select Folder".
 *
 * It also features a text field that updates to reflect the state of the dialog:
 * - "Folder chooser dialog is shown" when the dialog is opened.
 * - "Selected folder: [path_to_folder]" when a folder is successfully selected.
 * - "Folder chooser dialog was closed" when the dialog is cancelled.
 *
 * This serves as a practical example of how to integrate and manage the
 * [FolderChooserDialog], including handling folder selection and cancellation events,
 * within a Composable UI.
 *
 * @sample dialogs.file.FolderChooserDialogSample
 */
@Composable
fun FolderChooserDialogSample(){
    var showFolderChooserDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showFolderChooserDialog = true
                text = "Folder chooser dialog is shown"
            }
        ) {
            Text("Show Folder Chooser Dialog")
        }

        Text(text)
    }


    if (showFolderChooserDialog) {
        FolderChooserDialog(
            title = "Select Folder",
            onFolderSelected = {
                showFolderChooserDialog = false
                text = "Selected folder: ${it.absolutePath}"
            },
            onCancel = { showFolderChooserDialog = false; text = "Folder chooser dialog was closed" }
        )
    }
}