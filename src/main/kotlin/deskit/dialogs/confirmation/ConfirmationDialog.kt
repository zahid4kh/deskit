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

package deskit.dialogs.confirmation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.dialogs.info.toIntPx
import java.awt.Dimension

/**
 * Displays a modal dialog to request user confirmation for an action.
 *
 * This dialog is used when an action requires explicit user consent before proceeding. It includes
 * distinct "OK" and "Cancel" buttons, an optional icon, and a customizable content area.
 * It provides separate callbacks for confirm, cancel, and close actions.
 *
 * @param width The initial width of the dialog window.
 * @param height The initial height of the dialog window.
 * @param resizable Whether the user can resize the dialog window. Defaults to `false`.
 * @param title The text displayed in the dialog window's title bar.
 * @param message The default confirmation message. This is used by the default `content` lambda.
 *                If you provide a custom `content` composable, this parameter is ignored.
 * @param icon An optional `Painter` to be displayed at the top of the dialog.
 * @param iconSize The size of the `icon`.
 * @param iconTint The tint color applied to the `icon`. Defaults to the primary theme color.
 * @param confirmButtonText The text displayed on the confirmation (OK) button.
 * @param cancelButtonText The text displayed on the cancellation button.
 * @param onConfirm A callback function that is invoked when the user clicks the confirmation button.
 * @param onCancel A callback function that is invoked when the user clicks the cancel button.
 * @param onClose A callback function that is invoked when the user closes the dialog via the
 *                window's 'X' button. By default, this will also trigger `onCancel`.
 * @param content The main content of the dialog. By default, it displays the `message` text.
 *                This can be overridden with any custom Composable content.
 *
 * @sample deskit.dialogs.confirmation.ConfirmationDialogSample
 */
@Composable
fun ConfirmationDialog(
    width: Dp = 450.dp,
    height: Dp = 230.dp,
    resizable: Boolean = false,
    title: String = "Confirmation",
    message: String = "Please confirm to proceed",
    icon: Painter? = null,
    iconSize: DpSize = DpSize(64.dp, 64.dp),
    iconTint: Color = MaterialTheme.colorScheme.primary,
    confirmButtonText: String = "OK",
    cancelButtonText: String = "Cancel",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onClose: () -> Unit = onCancel,
    content: @Composable () -> Unit = {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
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
        resizable = resizable
    ) {
        if(resizable){
            window.minimumSize = Dimension(dialogWidth.toIntPx(), dialogHeight.toIntPx())
        }else{
            window.minimumSize = null
        }
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
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = iconTint,
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
 * @sample deskit.dialogs.confirmation.ConfirmationDialogSample
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