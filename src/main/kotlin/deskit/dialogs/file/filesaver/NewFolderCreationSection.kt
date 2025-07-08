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

