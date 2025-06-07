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

package deskit.dialogs.file

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
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
import deskit.dialogs.InfoDialog
import deskit.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
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
 * @sample deskit.dialogs.file.FolderChooserDialogSample
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

@Composable
private fun FilesAndFoldersListSection(
    coroutineScope: CoroutineScope,
    pathScrollState: ScrollState,
    items: List<File>,
    onFileClicked: () -> Unit,
    onFolderSelected: (File) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()){
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
                                .clickable {
                                    onFolderSelected(item)
                                    //currentDir = item
                                    coroutineScope.launch {
                                        pathScrollState.animateScrollTo(pathScrollState.maxValue)
                                    }
                                }
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
                            Text(item.name, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onFileClicked()
                                }
                                .padding(9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = getFileIcon(item),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), // Dimmed to indicate non-selectability
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.name,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Dimmed text
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
private fun BackButtonSection(
    coroutineScope: CoroutineScope,
    pathScrollState: ScrollState,
    onBackClicked: (File) -> Unit,
    currentDir: File
){
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
                            //currentDir = parent
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
            Text("Current Directory", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PathSegmentsSection(
    pathScrollState: ScrollState,
    pathSegments: List<File>,
    onFolderSelected: (File) -> Unit,
){
    Box(
        modifier = Modifier.fillMaxWidth()
    ){
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
 * @sample deskit.dialogs.file.FolderChooserDialogSample
 */
@Composable
private fun FolderChooserDialogSample(){
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