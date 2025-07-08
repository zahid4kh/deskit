package deskit.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import deskit.dialogs.info.InfoDialog
import deskit.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


internal fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1f MB".format(mb)
    val gb = mb / 1024.0
    return "%.1f GB".format(gb)
}

internal fun calculateFolderSize(folder: File): Long {
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

@Composable
internal fun getFileIcon(file: File): Painter {
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
        "tsx" -> painterResource(Res.drawable.react)
        "jsx" -> painterResource(Res.drawable.react)
        "c" -> painterResource(Res.drawable.c)
        "cpp" -> painterResource(Res.drawable.cplusplus)
        "h" -> painterResource(Res.drawable.h)
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


@Composable
internal fun FileInfoDialog(
    file: File,
    onClose: () -> Unit,
    resizable: Boolean
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
        resizable = resizable
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
                    .align(Alignment.BottomEnd)
                    .pointerHoverIcon(PointerIcon.Hand),
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
internal fun InfoRow(
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
