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
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.resources.*
import dialogs.InfoDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
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
 * - Directory navigation with clickable breadcrumb trail and horizontal scrollbar
 * - File browsing with vertical scrollbar
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
    val coroutineScope = rememberCoroutineScope()

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
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.height(8.dp))

                // Action buttons
                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium)
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
                        shape = MaterialTheme.shapes.medium
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


@Composable
private fun NavigationButtonsSection(
    coroutineScope: CoroutineScope,
    pathScrollState: ScrollState,
    currentDir: File,
    onBackClicked: (File) -> Unit,
    onNewFolderClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (currentDir.parentFile != null) {
                IconButton(
                    onClick = {
                        currentDir.parentFile?.let { parent ->
                            onBackClicked(parent)
                            coroutineScope.launch {
                                pathScrollState.animateScrollTo(pathScrollState.maxValue)
                            }
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
        ) {
            IconButton(onClick = onNewFolderClicked) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
            }
        }
    }
}


@Composable
private fun PathSegmentsSection(
    pathScrollState: ScrollState,
    pathSegments: List<File>,
    onFolderSelected: (File) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .horizontalScroll(pathScrollState)
                .padding(bottom = 8.dp, end = 12.dp)
        ) {
            pathSegments.forEachIndexed { index, dir ->
                Text(
                    text = dir.name.ifBlank { "." },
                    color = if (index == pathSegments.lastIndex)
                        MaterialTheme.colorScheme.primary else LocalContentColor.current,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onFolderSelected(dir) }
                        .padding(8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                if (index != pathSegments.lastIndex) {
                    Text("/", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        HorizontalScrollbar(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(end = 12.dp),
            adapter = rememberScrollbarAdapter(pathScrollState),
            style = LocalScrollbarStyle.current.copy(
                hoverColor = MaterialTheme.colorScheme.outline,
                unhoverColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}


@Composable
private fun FileNameInputSection(
    fileName: String,
    extension: String,
    onFileNameChanged: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = fileName,
            onValueChange = onFileNameChanged,
            label = { Text("File name", style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            placeholder = { Text("Enter filename${extension}",
                style = MaterialTheme.typography.bodyMedium) },
            supportingText = {
                Text(
                    "Extension $extension will be added automatically",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = extension,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
private fun FilesAndFoldersListSection(
    items: List<File>,
    onFolderClicked: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp)
            ) {
                items(items) { item ->
                    if (item.isDirectory) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onFolderClicked(item) }
                                .padding(9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.folder),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.name,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = getFileIcon(item),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.name,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState),
                style = LocalScrollbarStyle.current.copy(
                    hoverColor = MaterialTheme.colorScheme.outline,
                    unhoverColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun NewFolderCreationSection(
    visible: Boolean,
    folderName: String,
    onFolderNameChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onCreateFolder: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = folderName,
                onValueChange = onFolderNameChanged,
                label = { Text("New Folder Name", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large
            )
            Spacer(Modifier.width(3.dp))
            TextButton(
                onClick = onCancel
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.width(3.dp))
            OutlinedButton(
                onClick = onCreateFolder,
                enabled = folderName.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Create", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Composable
private fun getFileIcon(file: File): Painter {
    if (file.isDirectory) return painterResource(Res.drawable.folder)

    val extension = file.extension.lowercase()
    return when (extension) {
        // Images
        "webp" -> painterResource(Res.drawable.image)
        "png" -> painterResource(Res.drawable.png)
        "gif" -> painterResource(Res.drawable.gif)
        "bmp" -> painterResource(Res.drawable.bmp)
        "svg" -> painterResource(Res.drawable.svg)
        "jpg", "jpeg" -> painterResource(Res.drawable.jpg)

        // Docs
        "pdf" -> painterResource(Res.drawable.pdf)
        "doc", "docx" -> painterResource(Res.drawable.doc)
        "xls", "xlsx" -> painterResource(Res.drawable.xls)
        "ppt", "pptx" -> painterResource(Res.drawable.ppt)
        "txt" -> painterResource(Res.drawable.txt)
        "md" -> painterResource(Res.drawable.markdown)

        // Code
        "rb", "go", "rs" -> painterResource(Res.drawable.code)
        "js" -> painterResource(Res.drawable.javascript)
        "cs" -> painterResource(Res.drawable.csharp)
        "php" -> painterResource(Res.drawable.php)
        "ts" -> painterResource(Res.drawable.typescript)
        "c" -> painterResource(Res.drawable.c)
        "cpp" -> painterResource(Res.drawable.cplusplus)
        "java" -> painterResource(Res.drawable.java)
        "py" -> painterResource(Res.drawable.python)
        "kt" -> painterResource(Res.drawable.kotlin)
        "html", "htm" -> painterResource(Res.drawable.html5)
        "htmx" -> painterResource(Res.drawable.htmx)
        "css" -> painterResource(Res.drawable.css)
        "xml" -> painterResource(Res.drawable.xml)
        "yml", "yaml" -> painterResource(Res.drawable.yaml)
        "json" -> painterResource(Res.drawable.json)
        "db" -> painterResource(Res.drawable.sql)

        // Archive
        "rar", "7z", "tar", "gz" -> painterResource(Res.drawable.archive)
        "zip" -> painterResource(Res.drawable.zip)

        // Audio
        "mp3", "wav", "flac", "aac", "ogg" -> painterResource(Res.drawable.audio)

        // Video
        "mp4", "avi", "mkv", "mov", "wmv", "flv" -> painterResource(Res.drawable.video)

        // Fonts
        "ttf", "otf", "woff", "woff2" -> painterResource(Res.drawable.font)

        // Executable
        "app" -> painterResource(Res.drawable.exe)
        "exe" -> painterResource(Res.drawable.exe)
        "msi" -> painterResource(Res.drawable.msi)
        "rpm" -> painterResource(Res.drawable.rpm)
        "dmg" -> painterResource(Res.drawable.dmg)
        "deb" -> painterResource(Res.drawable.debian)

        else -> painterResource(Res.drawable.document)
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