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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import java.awt.Dimension
import java.io.File

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
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (index != pathSegments.lastIndex) {
                            Text("/", style = MaterialTheme.typography.labelLarge)
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
