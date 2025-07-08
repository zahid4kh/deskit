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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import deskit.dialogs.defaults.FileSaverColors
import deskit.resources.Res
import deskit.resources.folder
import deskit.utils.getFileIcon
import org.jetbrains.compose.resources.painterResource
import java.io.File


@Composable
internal fun FilesAndFoldersListSection(
    items: List<File>,
    onFolderClicked: (File) -> Unit,
    onShowFileInfo: (File) -> Unit,
    colors: FileSaverColors,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(colors.fileAndFolderListBG)
                    .padding(end = 12.dp)
            ) {
                items(items) { item ->
                    if (item.isDirectory) {
                        val folderInteractionSource = remember { MutableInteractionSource() }
                        val isFolderHovered by folderInteractionSource.collectIsHoveredAsState()

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onFolderClicked(item) }
                                .padding(9.dp)
                                .hoverable(folderInteractionSource),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    painter = painterResource(Res.drawable.folder),
                                    contentDescription = null,
                                    tint = colors.folderIconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.folderNameColor
                                )
                            }
                            AnimatedVisibility(
                                visible = isFolderHovered,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ){
                                IconButton(onClick = {onShowFileInfo(item)}, modifier = Modifier.size(20.dp)){
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Folder info",
                                        modifier = Modifier.size(20.dp),
                                        tint = colors.infoIconTint
                                    )
                                }
                            }
                        }
                    } else {
                        val fileInteractionSource = remember { MutableInteractionSource() }
                        val isFileHovered by fileInteractionSource.collectIsHoveredAsState()

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(9.dp)
                                .hoverable(fileInteractionSource),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    painter = getFileIcon(item),
                                    contentDescription = null,
                                    tint = colors.fileIconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    overflow = TextOverflow.Ellipsis,
                                    color = colors.fileNameColor,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            AnimatedVisibility(
                                visible = isFileHovered,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ){
                                IconButton(onClick = {onShowFileInfo(item)}, modifier = Modifier.size(20.dp)){
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "File info",
                                        modifier = Modifier.size(20.dp),
                                        tint = colors.infoIconTint
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
                    hoverColor = colors.scrollbarHoverColor,
                    unhoverColor = colors.scrollbarUnhoverColor
                )
            )
        }
    }
}
