/*
Copyright 2025 Zahid Khalilov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package deskit.dialogs.file.folderchooser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.dialogs.info.InfoDialog
import java.awt.Dimension
import java.io.File

/**
 * Displays a folder selection dialog with directory navigation and breadcrumb trail.
 *
 * This dialog allows users to browse and select folders from the file system. It displays
 * both files and directories, but only folders can be selected for the final result.
 * Files are shown with dimmed appearance and attempting to select them will display an
 * informational dialog. System items (those starting with ".") are hidden.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Choose Folder".
 * @param startDirectory The initial directory to display when the dialog opens.
 *                       Defaults to the user's Downloads folder.
 * @param onFolderSelected Callback function invoked with the selected File (directory) when
 *                         the user clicks Choose.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * Features:
 * - Path breadcrumb navigation with scrollbar
 * - Visual distinction between selectable folders and non-selectable files
 * - User guidance through InfoDialog when attempting to select files
 * - Folder content browsing with vertical scrollbar
 *
 * @sample deskit.dialogs.file.folderchooser.FolderChooserDialogSample
 */
@Composable
fun FolderChooserDialog(
    title: String = "Choose Folder",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    onFolderSelected: (File) -> Unit,
    onCancel: () -> Unit
) {
    var currentDir by remember { mutableStateOf(startDirectory) }
    var showFileNotAllowedDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val items = remember(currentDir) {
        currentDir.listFiles()
            ?.filter { !it.name.startsWith(".") }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

    val dialogState = rememberDialogState(size = DpSize(600.dp, 600.dp), position = WindowPosition(Alignment.Center))

    val pathScrollState = rememberScrollState()

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

                PathSegmentsSection(
                    pathScrollState = pathScrollState,
                    pathSegments = pathSegments,
                    onFolderSelected = { currentDir = it }
                )

                BackButtonSection(
                    coroutineScope = coroutineScope,
                    pathScrollState = pathScrollState,
                    onBackClicked = { currentDir = it },
                    currentDir = currentDir
                )

                Spacer(Modifier.height(8.dp))

                FilesAndFoldersListSection(
                    coroutineScope = coroutineScope,
                    pathScrollState = pathScrollState,
                    items = items,
                    onFileClicked = { showFileNotAllowedDialog = true },
                    onFolderSelected = { currentDir = it },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.height(8.dp))

                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onFolderSelected(currentDir) },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Choose")
                    }
                }
            }
        }
    }
    if (showFileNotAllowedDialog) {
        InfoDialog(
            title = "Cannot Select Files",
            message = "This dialog only allows selecting folders. Please choose a folder instead.",
            onClose = { showFileNotAllowedDialog = false }
        )
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
 * @sample deskit.dialogs.file.folderchooser.FolderChooserDialogSample
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