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
package deskit.dialogs.file.folderchooser

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File


@Composable
internal fun BackButtonSection(
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
