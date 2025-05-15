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

package dialogs.file


import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.CreateNewFolder
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
 * Displays a file save dialog with directory navigation, folder creation, and file naming capabilities.
 *
 * This dialog allows users to navigate through the file system, create new folders, and specify
 * a filename for saving. It includes file existence checking and animated folder creation UI.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Save As".
 * @param suggestedFileName The initial filename to populate in the text field. Can be empty.
 * @param extension The file extension to append to the saved file (e.g., ".txt", ".pdf").
 * @param onSave Callback function invoked with the selected File when the user clicks Save.
 * @param onCancel Callback function invoked when the user cancels the operation.
 *
 * Features:
 * - Directory navigation with clickable breadcrumb trail
 * - New folder creation with animated UI
 * - File existence validation
 * - Back button for parent directory navigation
 *
 * @sample dialogs.file.FileSaverDialogSample
 */
@Composable
fun FileSaverDialog(
    title: String = "Save As",
    suggestedFileName: String = "",
    extension: String,
    onSave: (File) -> Unit,
    onCancel: () -> Unit
) {
    val homeDir = remember { System.getProperty("user.home") }
    var fileName by remember { mutableStateOf(suggestedFileName) }
    var showFileExistsDialog by remember { mutableStateOf(false) }
    var currentDir by remember { mutableStateOf(File("$homeDir/Downloads")) }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

    var creatingNewFolder by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    val files = remember(currentDir) {
        currentDir.listFiles()
            ?.filter { !it.name.startsWith(".") }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }
    val dialogState = rememberDialogState(size = DpSize(600.dp, 600.dp), position = WindowPosition(Alignment.Center))

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onCancel
    ) {
        window.minimumSize = Dimension(600, 600)
        Surface{
            Column(Modifier.padding(16.dp)) {
                Text("Saving as", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { newValue ->
                            fileName = if (newValue.endsWith(extension, ignoreCase = true)) {
                                newValue.dropLast(extension.length)
                            } else {
                                newValue
                            }
                        },
                        label = { Text("File name") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large,
                        placeholder = { Text("Enter filename${extension}") },
                        supportingText = { Text("Extension $extension will be added automatically",
                            style = MaterialTheme.typography.bodySmall) }
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = extension,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(8.dp))


                Box(
                    modifier = Modifier.fillMaxWidth()
                ){
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
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { currentDir = dir }
                                    .padding(8.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            if (index != pathSegments.lastIndex) {
                                Text("/", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically,
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
                        Text("Choose Folder", style = MaterialTheme.typography.labelLarge)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                    ){
                        IconButton(onClick = { creatingNewFolder = true }) {
                            Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
                        }
                    }
                }

                AnimatedVisibility(
                    visible = creatingNewFolder,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newFolderName,
                            onValueChange = { newFolderName = it },
                            label = { Text("New Folder Name") },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large
                        )
                        Spacer(Modifier.width(3.dp))
                        TextButton(
                            onClick = { creatingNewFolder = false }
                        ){
                            Text("Cancel", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.width(3.dp))
                        OutlinedButton(
                            onClick = {
                                val newFolder = File(currentDir, newFolderName)
                                if (!newFolder.exists()) {
                                    newFolder.mkdir()
                                    currentDir = newFolder
                                }
                                creatingNewFolder = false
                                newFolderName = ""
                            },
                            enabled = newFolderName.isNotBlank(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Create")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn(Modifier.weight(1f)) {
                    items(files) { file ->
                        if (file.isDirectory) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { currentDir = file }
                                    .padding(9.dp)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(file.name)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) { Text("Cancel", color = MaterialTheme.colorScheme.error) }
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
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    if (showFileExistsDialog) {
        AlertDialog(
            onDismissRequest = { showFileExistsDialog = false },
            title = { Text("File Already Exists") },
            text = { Text("A file named \"$fileName\" already exists in this folder. Please choose a different name.") },
            confirmButton = {
                TextButton(onClick = { showFileExistsDialog = false }) {
                    Text("OK")
                }
            }
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
 *
 * @sample dialogs.file.FileSaverDialogSample
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