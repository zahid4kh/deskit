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

package deskit.dialogs.file.filechooser


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.utils.FileInfoDialog
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.io.File

/**
 * Displays a file selection dialog with smart file type icons and optional extension filtering.
 *
 * This dialog provides file system navigation with visual file type identification through
 * contextual icons. Files can be filtered by extension, and the dialog includes a breadcrumb
 * navigation trail.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Choose File".
 * @param startDirectory The initial directory to display when the dialog opens.
 *                       Defaults to the user's Downloads folder.
 * @param allowedExtensions Optional list of file extensions to filter by (e.g., ["txt", "pdf"]).
 *                          If null, all files are shown. Extensions are case-insensitive.
 * @param folderIconColor The color applied to folder icons. Defaults to tertiary theme color.
 * @param fileIconColor The color applied to file icons. Defaults to primary theme color.
 * @param onFileSelected Callback function invoked with the selected File when the user clicks a file.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * Features:
 * - Smart file type icons based on extension
 * - Directory and file navigation
 * - Bold breadcrumb trail with clickable segments
 * - Optional file extension filtering
 * - Color-coded folders with primary theme color
 *
 * @sample deskit.dialogs.file.filechooser.FileChooserDialogSample
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileChooserDialog(
    title: String = "Choose File",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    allowedExtensions: List<String>? = null,
    folderIconColor: Color = MaterialTheme.colorScheme.tertiary,
    fileIconColor: Color = MaterialTheme.colorScheme.primary,
    fileAndFolderListBG: Color = MaterialTheme.colorScheme.tertiaryContainer,
    folderNameColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    fileNameColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    badgeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    infoIconTint: Color = MaterialTheme.colorScheme.secondary,
    scrollbarHoverColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    scrollbarUnhoverColor: Color = MaterialTheme.colorScheme.inversePrimary,
    tooltipColor: Color = MaterialTheme.colorScheme.tertiary,
    resizableFileInfoDialog: Boolean = true,
    onFileSelected: (File) -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentDir by remember { mutableStateOf(startDirectory) }
    val files = remember(currentDir) {
        currentDir.listFiles()
            ?.filter {
                !it.name.startsWith(".") &&
                        (it.isDirectory || allowedExtensions == null || allowedExtensions.any {
                                ext -> it.name.endsWith(ext, ignoreCase = true)
                        })
            }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }

    var selectedFileForInfo by remember { mutableStateOf<File?>(null) }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

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

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.padding(16.dp)) {

                FileFilterSection(
                    allowedExtensions = allowedExtensions
                )

                Spacer(Modifier.height(8.dp))

                PathSegments(
                    pathSegments = pathSegments,
                    onPathSelected = { currentDir = it },
                    scrollState = pathScrollState
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        if (currentDir.parentFile != null) {
                            IconButton(
                                onClick = {
                                    currentDir.parentFile?.let { parent ->
                                        currentDir = parent
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowCircleLeft,
                                    contentDescription = "Back"
                                )
                            }
                        }
                        Spacer(Modifier.width(3.dp))
                        Text("Current Directory", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(8.dp))

                FileAndFolderSection(
                    files = files,
                    allowedExtensions = allowedExtensions,
                    folderIconColor = folderIconColor,
                    fileIconColor = fileIconColor,
                    fileAndFolderListBG = fileAndFolderListBG,
                    fileNameColor = fileNameColor,
                    folderNameColor = folderNameColor,
                    badgeColor = badgeColor,
                    badgeContentColor = badgeContentColor,
                    infoIconTint = infoIconTint,
                    scrollbarHoverColor = scrollbarHoverColor,
                    scrollbarUnhoverColor = scrollbarUnhoverColor,
                    tooltipColor = tooltipColor,
                    onDirectorySelected = {
                        currentDir = it
                        coroutineScope.launch {
                            pathScrollState.animateScrollTo(pathScrollState.maxValue)
                        }
                    },
                    onShowFileInfo = { file ->
                        selectedFileForInfo = file
                    },
                    onFileSelected = onFileSelected,
                    modifier = Modifier.weight(1f)
                )


                Spacer(Modifier.height(8.dp))

                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val selectedFile = currentDir.listFiles()?.firstOrNull { !it.isDirectory }
                            if (selectedFile != null) {
                                onFileSelected(selectedFile)
                            }
                        },
                        enabled = files.any { !it.isDirectory },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}


/**
 * A sample composable function demonstrating the usage of the [FileChooserDialog].
 *
 * This sample displays a button that, when clicked, shows a [FileChooserDialog].
 * The dialog is pre-configured with a title ("Open File") and a list of allowed
 * file extensions (e.g., "txt", "md", "png").
 *
 * It also features a simple text that updates to reflect the state of the dialog:
 * - "File chooser dialog is shown" when the dialog is opened.
 * - "Selected file: [path_to_file]" when a file is successfully selected.
 * - "File chooser dialog was closed" when the dialog is cancelled.
 *
 * This serves as a practical example of how to integrate and manage the
 * [FileChooserDialog], including handling file selection and cancellation events,
 * within a Composable UI.
 *
 * @sample deskit.dialogs.file.filechooser.FileChooserDialogSample
 */
@Composable
fun FileChooserDialogSample(){
    var showFileChooserDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showFileChooserDialog = true
                text = "File chooser dialog is shown"
            }
        ) {
            Text("Show File chooser Dialog")
        }

        Text(text)
    }


    if(showFileChooserDialog){
        FileChooserDialog(
            title = "Open File",
            allowedExtensions = listOf("txt", "md", "json", "kt", "py", "js", "html", "css", "png", "jpg"),
            onFileSelected = {
                showFileChooserDialog = false
                text = "Selected file: ${it.absolutePath}"
            },
            onCancel = { showFileChooserDialog = false; text = "File chooser dialog was closed" }
        )
    }
}