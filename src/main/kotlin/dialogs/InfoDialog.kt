package dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
 * Displays a modal information dialog with a custom title, message, and single OK button.
 *
 * This dialog is typically used to show non-critical information to the user that only requires
 * acknowledgment. The dialog is centered on screen and has a fixed size.
 *
 * @param title The title text displayed in the dialog window's title bar. Defaults to "Information".
 * @param message The main message text displayed in the center of the dialog. Defaults to "Information message".
 * @param onClose Callback function invoked when the user clicks OK or closes the dialog.
 *
 * @sample InfoDialogSample
 */
@Composable
fun InfoDialog(
    title: String = "Information",
    message: String = "Information message",
    onClose: () -> Unit
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

                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

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