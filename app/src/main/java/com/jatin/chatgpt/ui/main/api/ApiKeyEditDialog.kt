package com.jatin.chatgpt.ui.main.api

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jatin.chatgpt.R
import com.jatin.template.common.Constants
import com.jatin.template.common.GlobalConfig
import com.jatin.template.common.extension.toast

/**
 * api edit dialog
 *
 * @author Jatin
 * @time 30/04/2023
 */
@Composable
fun ApiKeyEditDialog(onCancel: () -> Unit, onFirm: (String) -> Unit) {
    var apiKey = ""
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = { onCancel.invoke() },
        title = { Text(text = stringResource(id = R.string.change_key)) },
        text = {
            DialogContent() {
                apiKey = it
            }
        },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(contentColor = Color(0xFFFFFFFF)),
                onClick = {
                if (apiKey.isEmpty()) {
                    ctx.toast(R.string.dialog_api_empty_tips)
                    return@Button
                }
                onFirm.invoke(apiKey)
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
    var text by remember { mutableStateOf(GlobalConfig.apiKey) }
    val ctx = LocalContext.current
    Column() {
        Text(text = stringResource(id = R.string.recommend_content))
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            modifier = Modifier
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(Constants.APPLY_URL)
                    ctx.startActivity(intent)
                },
            text = stringResource(id = R.string.apply),
            color = Color.LightGray,
            fontSize = 12.sp,
            textDecoration = TextDecoration.Underline
        )
        Spacer(modifier = Modifier.padding(10.dp))
        TextField(
            value = text,
            onValueChange = {
                text = it
                onChangeText.invoke(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
        )
        Spacer(modifier = Modifier.padding(15.dp))
        Text(
            text = stringResource(id = R.string.climate_caution),
            modifier = Modifier
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(Constants.CLIMATE_URL)
                    ctx.startActivity(intent)
                },
        color = Color.DarkGray,
        fontSize = 10.sp,
        )
    }
}

@Preview
@Composable
fun ApiKeyEditDialogPreview() {
    ApiKeyEditDialog({}, {})
}