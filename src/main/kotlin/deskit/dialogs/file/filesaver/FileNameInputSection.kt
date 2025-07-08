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

