package deskit.dialogs.file.filesaver

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
internal fun NewFolderCreationSection(
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

