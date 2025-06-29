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

package deskit.dialogs.file.filechooser

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import deskit.utils.getFileIcon
import org.jetbrains.compose.resources.painterResource
import deskit.resources.*
import java.io.File


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FileAndFolderSection(
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