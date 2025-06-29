package deskit.dialogs.file.filesaver

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
internal fun FileNameInputSection(
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

