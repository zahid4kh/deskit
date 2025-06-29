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


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.dialogs.InfoDialog
import deskit.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
 * @sample deskit.dialogs.file.FileChooserDialogSample
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileChooserDialog(
    title: String = "Choose File",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    allowedExtensions: List<String>? = null,
    folderIconColor: Color = MaterialTheme.colorScheme.tertiary,
    fileIconColor: Color = MaterialTheme.colorScheme.primary,
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
            onClose = { selectedFileForInfo = null }
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

@Composable
private fun FileFilterSection(
    allowedExtensions: List<String>?
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Choose File", style = MaterialTheme.typography.titleLarge)

            if (allowedExtensions != null && allowedExtensions.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "File filters",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                                    append("Allowed: ")
                                }
                                withStyle(
                                    SpanStyle(
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    append(allowedExtensions.joinToString(", ") { ".$it" })
                                }
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PathSegments(
    pathSegments: List<File>,
    onPathSelected: (File) -> Unit,
    scrollState: ScrollState
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.height(70.dp)
            //.border(1.dp, MaterialTheme.colorScheme.outline)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(bottom = 8.dp, end = 12.dp)
        ) {
            pathSegments.forEachIndexed { index, dir ->
                Text(
                    text = dir.name.ifBlank { "." },
                    color = if (index == pathSegments.lastIndex)
                        MaterialTheme.colorScheme.primary else LocalContentColor.current,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onPathSelected(dir) }
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
            adapter = rememberScrollbarAdapter(scrollState),
            style = LocalScrollbarStyle.current.copy(
                hoverColor = MaterialTheme.colorScheme.outline,
                unhoverColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileAndFolderSection(
    files: List<File>,
    allowedExtensions: List<String>?,
    folderIconColor: Color,
    fileIconColor: Color,
    onDirectorySelected: (File) -> Unit,
    onFileSelected: (File) -> Unit,
    onShowFileInfo: (File) -> Unit,
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier){
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
                    .padding(end = 12.dp)
            ) {
                items(files) { file ->
                    val folderInteractionSource = remember { MutableInteractionSource() }
                    val isFolderHovered by folderInteractionSource.collectIsHoveredAsState()

                    if (file.isDirectory) {
                        val matchingFilesCount = remember(file, allowedExtensions) {
                            if (allowedExtensions == null) {
                                null
                            } else {
                                file.listFiles()
                                    ?.count { childFile ->
                                        !childFile.isDirectory && allowedExtensions.any { ext ->
                                            childFile.name.endsWith(".$ext", ignoreCase = true)
                                        }
                                    } ?: 0
                            }
                        }

                        TooltipArea(
                            tooltip = {
                                Surface(
                                    modifier = Modifier,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        text = if (matchingFilesCount != null) {
                                            "$matchingFilesCount matching file${if (matchingFilesCount != 1) "s" else ""}"
                                        } else {
                                            "Folder: ${file.name}"
                                        },
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            delayMillis = 300,
                            tooltipPlacement = TooltipPlacement.CursorPoint(
                                offset = DpOffset(0.dp, 16.dp)
                            )
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { onDirectorySelected(file) }
                                    .padding(9.dp)
                                    .hoverable(folderInteractionSource),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.folder),
                                    contentDescription = null,
                                    tint = folderIconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(file.name, overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium)

                                    Row(verticalAlignment = Alignment.CenterVertically){
                                        AnimatedVisibility(
                                            visible = isFolderHovered,
                                            enter = scaleIn(),
                                            exit = scaleOut()
                                        ){
                                            IconButton(onClick = {onShowFileInfo(file)}, modifier = Modifier.size(20.dp)){
                                                Icon(
                                                    Icons.Default.Info,
                                                    contentDescription = "Folder/File info",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        if (matchingFilesCount != null && matchingFilesCount > 0) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text(
                                                    text = "$matchingFilesCount",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                        val fileInteractionSource = remember { MutableInteractionSource() }
                        val isFileHovered by fileInteractionSource.collectIsHoveredAsState()

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onFileSelected(file) }
                                .padding(9.dp)
                                .hoverable(fileInteractionSource),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = getFileIcon(file),
                                    contentDescription = null,
                                    tint = fileIconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(file.name, overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium)
                            }

                            AnimatedVisibility(
                                visible = isFileHovered,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ){
                                IconButton(onClick = {onShowFileInfo(file)}, modifier = Modifier.size(20.dp)){
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "File info",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
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
private fun FileInfoDialog(
    file: File,
    onClose: () -> Unit
) {
    val fileSize = remember(file) {
        if (file.isFile) {
            formatFileSize(file.length())
        } else {
            "Folder"
        }
    }

    val lastModified = remember(file) {
        val date = Date(file.lastModified())
        SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(date)
    }

    val fileExtension = remember(file) {
        if (file.isFile && file.extension.isNotEmpty()) {
            ".${file.extension}"
        } else {
            "N/A"
        }
    }

    val folderSize = remember(file) {
        if (file.isDirectory) {
            calculateFolderSize(file)
        } else {
            0L
        }
    }

    val totalFiles = remember(file) {
        if (file.isDirectory) {
            file.listFiles()?.size ?: 0
        } else {
            0
        }
    }

    InfoDialog(
        width = 400.dp,
        height = 320.dp,
        title = "File Information",
        onClose = onClose,
        resizable = true
    ) {
        val scrollState = rememberScrollState()
        Box{
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // File/Folder icon and name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = getFileIcon(file),
                        contentDescription = null,
                        tint = if (file.isDirectory) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider()

                // Metadata
                InfoRow("Type", if (file.isDirectory) "Folder" else "File")
                if (file.isFile) {
                    InfoRow("Extension", fileExtension)
                    InfoRow("Size", fileSize)
                }
                InfoRow("Location", file.parent ?: "Unknown")
                AnimatedVisibility(
                    visible = file.isDirectory
                ){
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ){
                        InfoRow("Total files", totalFiles.toString())
                        InfoRow("Folder size", formatFileSize(folderSize))
                    }
                }
                InfoRow("Modified", lastModified)
            }
            VerticalScrollbar(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .align(Alignment.BottomEnd),
                adapter = rememberScrollbarAdapter(scrollState),
                style = LocalScrollbarStyle.current.copy(
                    hoverColor = MaterialTheme.colorScheme.outline,
                    unhoverColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val isLocationLabel = label.contentEquals("Location")
    var isCopied by remember { mutableStateOf(false) }

    val locationModifier = if(isLocationLabel){
        Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable{
                clipboard.setContents(StringSelection(value), null)
                isCopied = true
            }
            .padding(5.dp)
    }else{
        Modifier
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    val displayMessage = if (isCopied) "Path Copied!!!" else value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
        if(isLocationLabel && value.length > 20){
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                state = rememberTooltipState(),
                tooltip = {
                    PlainTooltip {
                        Text(
                            text = if (isCopied) "Copied to clipboard!" else "Click to copy path"
                        )
                    }
                }
            ){
                Text(
                    text = displayMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCopied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = locationModifier.pointerHoverIcon(PointerIcon.Hand)
                )
            }
        }else{
            Text(
                text = displayMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCopied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = locationModifier
            )
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1f MB".format(mb)
    val gb = mb / 1024.0
    return "%.1f GB".format(gb)
}

private fun calculateFolderSize(folder: File): Long {
    var size = 0L
    try {
        folder.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateFolderSize(file)
            } else {
                file.length()
            }
        }
    } catch (e: Exception) {}
    return size
}


/**
 * Determines the appropriate Material icon for a given file based on its type and extension.
 *
 * This function analyzes the file extension to return a contextually appropriate icon from
 * the Material Icons Extended library. Directories receive a special folder icon with primary
 * color tinting.
 *
 * @param file The File object to analyze for icon selection.
 * @return ImageVector representing the most appropriate icon for the file type.
 *
 * Supported file types:
 * - **Images**: PNG, JPG, GIF, BMP, WebP, SVG
 * - **Documents**: PDF, Word, Excel, PowerPoint, TXT, Markdown
 * - **Code**: Kotlin, Java, JavaScript, Python, HTML, CSS, JSON, etc.
 * - **Archives**: ZIP, RAR, 7Z, TAR, GZ
 * - **Media**: Audio (MP3, WAV, FLAC) and Video (MP4, AVI, MKV)
 * - **Fonts**: TTF, OTF, WOFF
 * - **Executables**: EXE, APP, DEB, RPM
 *
 * Files without recognized extensions receive a generic file icon.
 */
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
 * @sample deskit.dialogs.file.FileChooserDialogSample
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