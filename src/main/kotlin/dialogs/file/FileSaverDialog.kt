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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import java.io.File

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
        onCloseRequest = onCancel,
        resizable = false
    ) {
        Surface{
            Column(Modifier.padding(16.dp)) {
                Text("Saving as", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File name") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(extension)
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
                                style = MaterialTheme.typography.labelLarge
                            )
                            if (index != pathSegments.lastIndex) {
                                Text("/", style = MaterialTheme.typography.labelLarge)
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
                            val finalFile = File(currentDir, fileName)
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