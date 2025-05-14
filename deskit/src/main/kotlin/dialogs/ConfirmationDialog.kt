package dialogs


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState

@Composable
fun ConfirmationDialog(
    title: String = "Title",
    onClose: () -> Unit
){
    val fixedWidth = 450.dp
    val fixedHeight = 230.dp

    val dialogState = rememberDialogState(
        size = DpSize(fixedWidth, fixedHeight),
        position = WindowPosition(Alignment.Center)
    )

    DialogWindow(
        title = title,
        state = dialogState,
        onCloseRequest = {onClose()},
        resizable = false
    ) {

        Surface(
            modifier = Modifier.fillMaxSize()
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Please confirm to proceed",
                    modifier = Modifier.offset(y = (-30).dp),
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.align(Alignment.BottomEnd)
                ){
                    Button(
                        onClick = {onClose()}
                    ){
                        Text(
                            text = "Cancel"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onClose() }) { Text("OK") }
                }
            }
        }
    }
}