package deskit.dialogs.file.filesaver

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.CreateNewFolder
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
internal fun NavigationButtonsSection(
    coroutineScope: CoroutineScope,
    pathScrollState: ScrollState,
    currentDir: File,
    onBackClicked: (File) -> Unit,
    onNewFolderClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (currentDir.parentFile != null) {
                IconButton(
                    onClick = {
                        currentDir.parentFile?.let { parent ->
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
            Text("Choose Folder", style = MaterialTheme.typography.labelLarge)
        }

        Box(
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onNewFolderClicked) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
            }
        }
    }
}

