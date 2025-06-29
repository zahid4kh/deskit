package deskit.dialogs.file.folderchooser

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import deskit.resources.Res
import deskit.resources.folder
import deskit.utils.getFileIcon
import kotlinx.coroutines.*
import org.jetbrains.compose.resources.painterResource
import java.io.File


@Composable
internal fun FilesAndFoldersListSection(
    coroutineScope: CoroutineScope,
    pathScrollState: ScrollState,
    items: List<File>,
    onFileClicked: () -> Unit,
    onFolderSelected: (File) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp)
            ) {
                items(items) { item ->
                    if (item.isDirectory) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onFolderSelected(item)
                                    //currentDir = item
                                    coroutineScope.launch {
                                        pathScrollState.animateScrollTo(pathScrollState.maxValue)
                                    }
                                }
                                .padding(9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.folder),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(item.name, overflow = TextOverflow.Ellipsis)
                        }
                    } else {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onFileClicked()
                                }
                                .padding(9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = getFileIcon(item),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), // Dimmed to indicate non-selectability
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.name,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // Dimmed text
                                style = MaterialTheme.typography.bodyMedium
                            )
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
