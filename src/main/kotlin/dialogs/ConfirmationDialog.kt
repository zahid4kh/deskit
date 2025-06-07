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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState

/**
 * Displays a modal confirmation dialog with Cancel and OK buttons, and an optional icon.
 *
 * This dialog is used when an action requires user confirmation before proceeding.
 * The dialog provides separate callbacks for confirm and cancel actions, allowing
 * different behaviors for each button.
 *
 * @param title The title text displayed in the dialog window's title bar.
 * @param message The confirmation message displayed to the user.
 * @param icon Optional icon/image to display in the dialog to enhance visual appeal.
 * @param iconSize Size of the icon if provided. Defaults to 64.dp.
 * @param colorFilter Color filter to apply to the icon if provided.
 * @param confirmButtonText Text for the confirmation button. Defaults to "OK".
 * @param cancelButtonText Text for the cancel button. Defaults to "Cancel".
 * @param content Optional composable content to replace the standard message. Can be used for
 *                custom layout, rich text, or more complex confirmation displays.
 * @param onConfirm Callback function invoked when the user clicks the confirmation button.
 * @param onCancel Callback function invoked when the user clicks Cancel or the X button.
 * @param onClose Callback function invoked when the dialog is closed via the window's X button.
 *                Defaults to calling onCancel.
 *
 * @sample dialogs.ConfirmationDialogSample
 */
@Composable
fun ConfirmationDialog(
    width: Dp = 450.dp,
    height: Dp = 230.dp,
    title: String = "Confirmation",
    message: String = "Please confirm to proceed",
    icon: Painter? = null,
    iconSize: DpSize = DpSize(64.dp, 64.dp),
    colorFilter: ColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
    confirmButtonText: String = "OK",
    cancelButtonText: String = "Cancel",
    content: @Composable () -> Unit = {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    },
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onClose: () -> Unit = onCancel
) {
//    val dialogWidth = if (icon != null) 500.dp else 450.dp
//    val dialogHeight = if (icon != null) 280.dp else 230.dp

    val dialogWidth = width
    val dialogHeight = height

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

                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(cancelButtonText)
                    }
                    Button(
                        onClick = onConfirm,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}

/**
 * A sample composable function demonstrating the usage of the [ConfirmationDialog].
 *
 * This sample displays a button that, when clicked, shows a [ConfirmationDialog].
 * It also features a text field that updates to reflect the state of the dialog
 * (e.g., "Confirmation dialog is shown", "Confirm was clicked", "Cancel was clicked",
 * or "Confirmation dialog was closed"). This serves as a practical example of how to
 * integrate and manage the [ConfirmationDialog] and its different dismissal/action
 * callbacks within a Composable UI.
 * @sample dialogs.ConfirmationDialogSample
 */
@Composable
fun ConfirmationDialogSample(){
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                showConfirmationDialog = true
                text = "Confirmation dialog is shown"
            }
        ) {
            Text("Show Confirmation Dialog")
        }

        Text(text)
    }


    if(showConfirmationDialog){
        ConfirmationDialog(
            onClose = {showConfirmationDialog = false; text = "Confirmation dialog was closed"},
            onConfirm = {showConfirmationDialog = false; text = "Confirm was clicked"},
            onCancel = {showConfirmationDialog = false; text = "Cancel was clicked"}
        )
    }
}