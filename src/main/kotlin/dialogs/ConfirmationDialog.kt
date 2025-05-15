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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState

/**
 * Displays a modal confirmation dialog with Cancel and OK buttons.
 *
 * This dialog is used when an action requires user confirmation before proceeding.
 * The dialog provides separate callbacks for confirm and cancel actions, allowing
 * different behaviors for each button.
 *
 * @param title The title text displayed in the dialog window's title bar.
 * @param message The confirmation message displayed to the user.
 * @param onConfirm Callback function invoked when the user clicks the OK button.
 * @param onCancel Callback function invoked when the user clicks Cancel or the X button.
 * @param onClose Callback function invoked when the dialog is closed via the window's X button.
 *                Defaults to calling onCancel.
 *
 * @sample dialogs.ConfirmationDialogSample
 */
@Composable
fun ConfirmationDialog(
    title: String = "Confirmation",
    message: String = "Please confirm to proceed",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onClose: () -> Unit = onCancel
) {
    val dialogState = rememberDialogState(
        size = DpSize(450.dp, 230.dp),
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
                Spacer(Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Button(onClick = onConfirm) {
                        Text("OK")
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