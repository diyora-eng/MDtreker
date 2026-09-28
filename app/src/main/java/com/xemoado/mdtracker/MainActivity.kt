package com.xemoado.mdtracker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("registration") }

            when (currentScreen) {
                "registration" -> {
                    RegistrationScreen(
                        onNavigateToEntry = { currentScreen = "entry" }
                    )
                }
                "entry" -> {
                    BaseScreen(
                        onNavigateToHistory = { currentScreen = "history" },
                        onNavigateBack = { currentScreen = "registration" }
                    )
                }
                "history" -> {
                    HistoryScreen(
                        onNavigateBack = { currentScreen = "entry" }
                    )
                }
            }
        }
    }
}
