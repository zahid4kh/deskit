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

package dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState

/**
 * Displays a modal information dialog with a custom title, message, optional image, and single OK button.
 *
 * This dialog is typically used to show non-critical information to the user that only requires
 * acknowledgment. The dialog is centered on screen and has a size that adapts to content.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Information".
 * @param message The main message text displayed in the center of the dialog. Defaults to "Information message".
 * @param icon Optional icon/image to display at the top of the dialog to make it more visually appealing.
 * @param colorFilter Color filter to apply to the icon if provided.
 * @param iconSize Size of the icon if provided. Defaults to 64.dp.
 * @param content Optional composable content to replace the standard message. Can be used for
 *                custom layout, rich text, or more complex information displays.
 * @param onClose Callback function invoked when the user clicks OK or closes the dialog.
 *
 * @sample dialogs.InfoDialogSample
 */
@Composable
fun InfoDialog(
    title: String = "Information",
    message: String = "Information message",
    icon: Painter? = null,
    colorFilter: ColorFilter? = ColorFilter.tint(MaterialTheme.colorScheme.primary),
    iconSize: DpSize = DpSize(64.dp, 64.dp),
    content: @Composable () -> Unit = {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    },
    onClose: () -> Unit
) {
    val dialogWidth = if (icon != null) 500.dp else 450.dp
    val dialogHeight = if (icon != null) 280.dp else 230.dp

    val dialogState = rememberDialogState(
        size = DpSize(dialogWidth, dialogHeight),
        position = WindowPosition(Alignment.Center)
    )

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = onClose,
        resizable = false
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    Spacer(Modifier.height(4.dp))
                    Image(
                        painter = icon,
                        contentDescription = null,
                        colorFilter = colorFilter,
                        modifier = Modifier.size(iconSize.width, iconSize.height)
                    )
                    Spacer(Modifier.height(16.dp))
                } else {
                    Spacer(Modifier.height(8.dp))
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.End),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("OK")
                }
            }
        }
    }
}

/**
 * A sample composable function demonstrating the usage of the [InfoDialog].
 *
 * This sample displays a button that, when clicked, shows an [InfoDialog].
 * It also displays a text field that updates to reflect the state of the dialog
 * (e.g., "Info dialog is shown", "Info dialog was closed").
 * This serves as a practical example of how to integrate and manage the
 * [InfoDialog] within a Composable UI.
 * @sample dialogs.InfoDialogSample
 */
@Composable
fun InfoDialogSample(){
    var showInfoDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showInfoDialog = true
                text = "Info dialog is shown"
            }
        ) {
            Text("Show Info Dialog")
        }

        Text(text)
    }


    if (showInfoDialog){
        InfoDialog(
            onClose = { showInfoDialog = false; text = "Info dialog was closed" }
        )
    }
}