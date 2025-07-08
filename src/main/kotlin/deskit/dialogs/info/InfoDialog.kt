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

package deskit.dialogs.info

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import deskit.dialogs.defaults.InfoDialogColors
import deskit.dialogs.defaults.InfoDialogDefaults
import java.awt.Dimension

/**
 * Displays a modal information dialog.
 *
 * This dialog is designed to present information that requires user acknowledgment. It features a
 * title, a customizable content area, an optional icon, and a single "OK" button to close it.
 *
 * @param width The initial width of the dialog window.
 * @param height The initial height of the dialog window.
 * @param resizable Whether the user can resize the dialog window. Defaults to `false`.
 * @param title The text displayed in the dialog window's title bar.
 * @param message The default message text. This is used by the default `content` lambda.
 *                If you provide a custom `content` composable, this parameter is ignored.
 * @param icon An optional `Painter` to be displayed at the top of the dialog, above the content.
 * @param colors Default colors for Info dialog.
 * @param iconSize The size of the `icon`.
 * @param onClose A callback function that is invoked when the user clicks the "OK" button or
 *                closes the dialog window.
 * @param content The main content of the dialog. By default, it displays the `message` text.
 *                This can be overridden with any custom Composable content for more complex layouts.
 *
 * @sample InfoDialogSample
 */
@Composable
fun InfoDialog(
    width: Dp = 450.dp,
    height: Dp = 230.dp,
    resizable: Boolean = false,
    title: String = "Information",
    message: String = "Information message",
    icon: Painter? = null,
    colors: InfoDialogColors = InfoDialogDefaults.colors(),
    iconSize: DpSize = DpSize(64.dp, 64.dp),
    onClose: () -> Unit,
    content: @Composable () -> Unit = {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
) {
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
        resizable = resizable,
        alwaysOnTop = true
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
                        tint = colors.iconTint,
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
                    modifier = Modifier.align(Alignment.End).pointerHoverIcon(PointerIcon.Hand),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.okButtonColor)
                ) {
                    Text("OK", color = colors.okButtonTextColor)
                }
            }
        }
    }
}


@Composable
internal fun Dp.toIntPx(): Int {
    val density = LocalDensity.current
    return with(density) { this@toIntPx.toPx().toInt() }
}

/**
 * A sample composable function demonstrating the usage of the [InfoDialog].
 *
 * This sample displays a button that, when clicked, shows an [InfoDialog].
 * It also displays a text field that updates to reflect the state of the dialog
 * (e.g., "Info dialog is shown", "Info dialog was closed").
 * This serves as a practical example of how to integrate and manage the
 * [InfoDialog] within a Composable UI.
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