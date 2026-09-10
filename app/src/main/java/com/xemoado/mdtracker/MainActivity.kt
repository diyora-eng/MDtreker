package com.xemoado.mdtracker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("main") }

            when (currentScreen) {
                "main" -> {
                    MainScreen(
                        onNavigateToBase = { currentScreen = "base" }
                    )
                }
                "base" -> {
                    BaseScreen()
                }
            }
        }
    }
}