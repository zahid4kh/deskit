package dialogs.file


import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
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
 * @sample dialogs.file.FileChooserDialogSample
 */
@Composable
fun FileChooserDialog(
    title: String = "Choose File",
    startDirectory: File = File(System.getProperty("user.home") + "/Downloads"),
    allowedExtensions: List<String>? = null,
    onFileSelected: (File) -> Unit,
    onCancel: () -> Unit
) {
    var currentDir by remember { mutableStateOf(startDirectory) }
    val files = remember(currentDir) {
        currentDir.listFiles()
            ?.filter {
                !it.name.startsWith(".") && (it.isDirectory ||
                        allowedExtensions == null ||
                        allowedExtensions.any {
                            ext -> it.name.endsWith(ext, ignoreCase = true)
                        })
            }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?: emptyList()
    }

    val pathSegments = generateSequence(currentDir) { it.parentFile }
        .toList()
        .asReversed()

    val dialogState = rememberDialogState(size = DpSize(600.dp, 600.dp), position = WindowPosition(Alignment.Center))

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onCancel,
        resizable = false
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.padding(16.dp)) {
                Text("Current Directory", style = MaterialTheme.typography.titleLarge)

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

                LazyColumn(Modifier.weight(1f)) {
                    items(files) { file ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    if (file.isDirectory) currentDir = file
                                    else onFileSelected(file)
                                }
                                .padding(9.dp)
                        ) {
                            Icon(
                                imageVector = getFileIcon(file),
                                contentDescription = null,
                                tint = if (file.isDirectory) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(file.name)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.align(Alignment.End)) {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                }
            }
        }
    }
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
private fun getFileIcon(file: File): ImageVector {
    if (file.isDirectory) return Icons.Default.Folder

    val extension = file.extension.lowercase()
    return when (extension) {
        // Images
        "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg" -> Icons.Default.Image

        // Docs
        "pdf" -> Icons.Default.PictureAsPdf
        "doc", "docx" -> Icons.Default.Description
        "xls", "xlsx" -> Icons.Default.TableChart
        "ppt", "pptx" -> Icons.Default.Slideshow
        "txt" -> Icons.AutoMirrored.Filled.TextSnippet
        "md" -> Icons.AutoMirrored.Filled.Article

        // Code
        "kt", "java", "js", "ts", "py", "cpp", "c", "cs", "php", "rb", "go", "rs" -> Icons.Default.Code
        "html", "htm" -> Icons.Default.Html
        "css" -> Icons.Default.Css
        "json", "xml", "yaml", "yml" -> Icons.Default.DataObject

        // Archive
        "zip", "rar", "7z", "tar", "gz" -> Icons.Default.Archive

        // Audio
        "mp3", "wav", "flac", "aac", "ogg" -> Icons.Default.AudioFile

        // Video
        "mp4", "avi", "mkv", "mov", "wmv", "flv" -> Icons.Default.VideoFile

        // Fonts
        "ttf", "otf", "woff", "woff2" -> Icons.Default.FontDownload

        // Executable
        "exe", "app", "deb", "rpm" -> Icons.Default.Storage

        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }
}


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