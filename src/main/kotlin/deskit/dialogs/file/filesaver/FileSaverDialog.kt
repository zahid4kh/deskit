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
package deskit.dialogs.file.filesaver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.dialogs.defaults.FileSaverColors
import deskit.dialogs.defaults.FileSaverDefaults
import deskit.dialogs.info.InfoDialog
import deskit.utils.FileInfoDialog
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.io.File


/**
 * Displays a file save dialog with directory navigation, folder creation, and file naming capabilities.
 *
 * This dialog allows users to navigate through the file system, create new folders, and specify
 * a filename for saving. It includes file existence checking and animated folder creation UI.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Save As".
 * @param suggestedFileName The initial filename to populate in the text field. Can be empty.
 * @param extension The file extension to append to the saved file (e.g., ".txt", ".pdf").
 * @param resizableFileInfoDialog Whether the file info dialog can be resized. Defaults to `true`.
 * @param onSave Callback function invoked with the selected File when the user clicks Save.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * Features:
 * - Directory navigation with clickable breadcrumb trail and horizontal scrollbar
 * - File browsing with vertical scrollbar
 * - New folder creation with animated UI
 * - File existence validation
 * - Back button for parent directory navigation
 *
 * @sample deskit.dialogs.file.filesaver.FileSaverDialogSample
 */
@Composable
fun FileSaverDialog(
    title: String = "Save As",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    suggestedFileName: String = "",
    colors: FileSaverColors = FileSaverDefaults.colors(),
    extension: String,
    resizableFileInfoDialog: Boolean = true,
    onSave: (File) -> Unit,
    onCancel: () -> Unit
) {
    //val homeDir = remember { System.getProperty("user.home") }
    var fileName by remember { mutableStateOf(suggestedFileName) }
    var showFileExistsDialog by remember { mutableStateOf(false) }
    var currentDir by remember { mutableStateOf(startDirectory) }
    val coroutineScope = rememberCoroutineScope()
    var selectedFileForInfo by remember { mutableStateOf<File?>(null) }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

    var creatingNewFolder by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    val items = remember(currentDir) {
        currentDir.listFiles()
            ?.filter { !it.name.startsWith(".") }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }

    val dialogState = rememberDialogState(size = DpSize(600.dp, 600.dp), position = WindowPosition(Alignment.Center))
    val pathScrollState = rememberScrollState()

    LaunchedEffect(pathSegments) {
        pathScrollState.animateScrollTo(pathScrollState.maxValue)
    }

    selectedFileForInfo?.let { file ->
        FileInfoDialog(
            file = file,
            onClose = { selectedFileForInfo = null },
            resizable = resizableFileInfoDialog
        )
    }

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onCancel
    ) {
        window.minimumSize = Dimension(600, 600)
        window.undecoratedResizerThickness = 2.dp
        Surface(
            modifier = Modifier.fillMaxSize()
        ){
            Column(Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
                Text("Saving as", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                // Filename input field
                FileNameInputSection(
                    fileName = fileName,
                    extension = extension,
                    onFileNameChanged = { newValue ->
                        fileName = if (newValue.endsWith(extension, ignoreCase = true)) {
                            newValue.dropLast(extension.length)
                        } else {
                            newValue
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Path segments with scrollbar
                PathSegmentsSection(
                    pathScrollState = pathScrollState,
                    pathSegments = pathSegments,
                    onFolderSelected = { currentDir = it }
                )

                // Navigation row with Back button and New Folder button
                NavigationButtonsSection(
                    coroutineScope = coroutineScope,
                    pathScrollState = pathScrollState,
                    currentDir = currentDir,
                    onBackClicked = { currentDir = it },
                    onNewFolderClicked = { creatingNewFolder = true }
                )

                // New folder creation UI
                NewFolderCreationSection(
                    visible = creatingNewFolder,
                    folderName = newFolderName,
                    onFolderNameChanged = { newFolderName = it },
                    onCancel = { creatingNewFolder = false },
                    onCreateFolder = {
                        val newFolder = File(currentDir, newFolderName)
                        if (!newFolder.exists()) {
                            newFolder.mkdir()
                            currentDir = newFolder
                        }
                        creatingNewFolder = false
                        newFolderName = ""
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Files and folders list with scrollbar
                FilesAndFoldersListSection(
                    items = items,
                    onFolderClicked = {
                        currentDir = it
                        coroutineScope.launch {
                            pathScrollState.animateScrollTo(pathScrollState.maxValue)
                        }
                    },
                    onShowFileInfo = {file ->
                        selectedFileForInfo = file
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.height(8.dp))

                // Action buttons
                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalFileName = if (fileName.endsWith(extension, ignoreCase = true)) {
                                fileName
                            } else {
                                fileName + extension
                            }
                            val finalFile = File(currentDir, finalFileName)
                            if (finalFile.exists()) {
                                showFileExistsDialog = true
                            } else {
                                onSave(finalFile)
                            }
                        },
                        enabled = fileName.isNotBlank(),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Save", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    if (showFileExistsDialog) {
        val displayFileName = if (fileName.endsWith(extension, ignoreCase = true)) {
            fileName
        } else {
            fileName + extension
        }

        InfoDialog(
            title = "File already exists",
            message = "A file named \"$displayFileName\" already exists in this folder. Please choose a different name.",
            onClose = { showFileExistsDialog = false }
        )
    }
}


/**
 * A sample composable function demonstrating the usage of the [FileSaverDialog].
 *
 * This sample displays a button that, when clicked, shows a [FileSaverDialog].
 * The dialog is pre-configured with a title ("Save As"), a suggested filename ("newfile"),
 * and a file extension (".md").
 *
 * It also features a text field that updates to reflect the state of the dialog:
 * - "File saver dialog is shown" when the dialog is opened.
 * - "File was saved and dialog was closed" when a file is successfully saved.
 *   In this sample, upon saving, "# Kotlin is fun" is written to the chosen file.
 * - "File saver dialog was closed" when the dialog is cancelled.
 *
 * This serves as a practical example of how to integrate and manage the
 * [FileSaverDialog], including handling file saving and cancellation events,
 * within a Composable UI.
 */
@Composable
fun FileSaverDialogSample(){
    var showFileSaverDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showFileSaverDialog = true
                text = "File saver dialog is shown"
            }
        ) {
            Text("Show File Saver Dialog")
        }

        Text(text)
    }


    if(showFileSaverDialog){
        FileSaverDialog(
            title = "Save As",
            suggestedFileName = "newfile",
            extension = ".md",
            onSave = {
                it.writeText("# Kotlin is fun")
                showFileSaverDialog = false; text = "File was saved and dialog was closed"
            },
            onCancel = { showFileSaverDialog = false; text = "File saver dialog was closed" }
        )
    }
}