package com.jatin.chatgpt.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.jatin.chatgpt.R
import com.jatin.chatgpt.model.Template
import com.jatin.chatgpt.ui.main.api.ApiKeyEditDialog
import com.jatin.chatgpt.ui.main.template.TemplateAddDialog
import com.jatin.chatgpt.ui.main.template.TemplateListDialog
import com.jatin.template.common.Constants
import com.jatin.template.common.GlobalConfig
import com.jatin.template.common.extension.toast

/**
 * The popwindow of the home page
 *
 * @author Jatin
 * @time 30/04/2023
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPop(
    templateList: List<Template>,
    count: Int,
    onCloseCallback: () -> Unit,
    onSaveTemplate: (String) -> Unit,
    onTemplateLoad: (Template) -> Unit,
    onTemplateDelete: (Template) -> Unit,
) {
    var isShowAdd by remember { mutableStateOf(false) }
    var isShowList by remember { mutableStateOf(false) }
    var isShowEditKey by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    Popup(
        offset = IntOffset(0, 150),
        alignment = Alignment.TopEnd,
        onDismissRequest = { onCloseCallback.invoke() },
        content = {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.width(200.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (count > 0) {
                                    isShowAdd = true
                                } else {
                                    ctx.toast(R.string.dialog_template_add_tips)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton({}) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                        Text(text = stringResource(id = R.string.more_templates_create))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isShowList = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton({}) {
                            Icon(Icons.Filled.Create, contentDescription = null)
                        }
                        Text(text = stringResource(id = R.string.more_template_list))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isShowEditKey = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton({}) {
                            Icon(Icons.Filled.Lock, contentDescription = null)
                        }
                        Text(text = stringResource(id = R.string.change_key))
                    }
                }
            }

        }
    )

    if (isShowAdd) {
        TemplateAddDialog(onCancel = { isShowAdd = false }, onFirm = {
            onSaveTemplate.invoke(it)
            isShowAdd = false
            onCloseCallback.invoke()
            ctx.toast(R.string.dialog_save_success_tips)
        })
    }
    if (isShowList) {
        TemplateListDialog(templateList, onCancel = {
            isShowList = false
        }, onLoad = {
            isShowList = false
            onTemplateLoad.invoke(it)
        }, onDelete = { onTemplateDelete.invoke(it) })
    }
    if (isShowEditKey) {
        ApiKeyEditDialog(onCancel = {
            isShowEditKey = false
        }, onFirm = {
            GlobalConfig.apiKey = it
            ctx.toast(R.string.dialog_key_change_tips)
            isShowEditKey = false
        })
    }
}

@Preview
@Composable
fun PopPreview() {
    MainPop(mutableListOf(), 1, {}, {}, {}, {})
}
