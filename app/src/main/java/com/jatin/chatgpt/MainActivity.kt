package com.jatin.chatgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jatin.chatgpt.ui.main.MainPage
import com.jatin.chatgpt.ui.theme.ChatgptTheme
import org.koin.androidx.compose.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatgptTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.Black
                ) {
                    MainPage(get())
                }
            }
        }
    }
}
