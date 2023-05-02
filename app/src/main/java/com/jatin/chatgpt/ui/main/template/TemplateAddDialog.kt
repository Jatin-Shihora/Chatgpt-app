package com.jatin.chatgpt.ui.main.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jatin.chatgpt.R
import com.jatin.template.common.extension.toast

/**
 * Added dialog in the template
 *
 * @author Jatin
 * @time 30/04/2023
 */
@Composable
fun TemplateAddDialog(onCancel: () -> Unit, onFirm: (String) -> Unit) {
    var text = ""
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = { onCancel.invoke() },
        title = { Text(text = stringResource(id = R.string.more_templates_create)) },
        text = {
            DialogContent() {
                text = it
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFFFFFFF)),
                onClick = {             //Prompt is not empty
                    if (text.isEmpty()) {
                    ctx.toast(R.string.dialog_template_empty_tips)
                    return@Button
                }
                onFirm.invoke(text)
            }) {
                Text(text = stringResource(id = R.string.dialog_confirm))
            }
        },
        dismissButton = {
            Button(colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFFFFFFF)),
                onClick = {
                onCancel.invoke()
            }) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogContent(onChangeText: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = {
            text = it
            onChangeText.invoke(text)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp),
        maxLines = 1
    )
}

@Preview
@Composable
fun TemplateAddDialogPreview() {
    TemplateAddDialog({}, {})
}