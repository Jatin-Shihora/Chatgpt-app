package com.jatin.chatgpt.ui.main.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jatin.chatgpt.R

/**
 * General prompt dialog
 *
 * @author Jatin
 * @time 30/04/2023
 */
@Composable
fun CommonTipsDialog(text: String, onCancel: () -> Unit, onFirm: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = { onCancel.invoke() },
        title = { Text(text = text) },
        confirmButton = {
            Button(onClick = {             //Prompt is not empty
                onFirm.invoke(text)
            }) {
                Text(text = stringResource(id = R.string.dialog_confirm))
            }
        },
        dismissButton = {
            Button(onClick = {
                onCancel.invoke()
            }) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        },
    )
}


@Preview
@Composable
fun CommonTipsDialogPreview() {
    CommonTipsDialog("hhhh", {}, {})
}