package com.jatin.chatgpt.widget

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.delay

/**
 * @author Jatin
 * @time 30/04/2023
 */

@Composable
fun TextCursorBlinking(text: String, modifier: Modifier = Modifier) {
    var showCursor by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            showCursor = !showCursor
        }
    }
    BasicTextField(
        value = if (showCursor) "$text|" else text,
        onValueChange = { },
        modifier = modifier,
        readOnly = true,
        cursorBrush = SolidColor(Color.White) // 光标的颜色
    )
}