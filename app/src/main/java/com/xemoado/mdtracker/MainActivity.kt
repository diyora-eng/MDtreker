package com.xemoado.mdtracker

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Tab(val title: String, val icon: ImageVector) {
    MAIN("Главная", Icons.Default.Home),
    TEST("Тест MDS", Icons.Default.Star),
    HISTORY("Записи", Icons.AutoMirrored.Filled.List),
    PROFILE("Профиль", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot(
    viewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    var isRegistered by rememberSaveable { mutableStateOf(false) }

    if (!isRegistered) {
        RegistrationScreen(
            onRegisterSuccess = { nickname ->
                viewModel.updateUserName(nickname)
                isRegistered = true
            }
        )
    } else {
        MainContent(viewModel = viewModel)
    }
}

@Composable
fun MainContent(
    viewModel: TrackerViewModel
) {
    var currentTab by rememberSaveable { mutableStateOf(Tab.MAIN) }
    val entries by viewModel.entries.collectAsState(initial = emptyList())
    val userName by viewModel.userName.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = tab == currentTab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                Tab.MAIN -> MainScreen(viewModel = viewModel)
                Tab.TEST -> TestScreen()
                Tab.HISTORY -> HistoryScreen(viewModel = viewModel)
                Tab.PROFILE -> ProfileScreen(
                    state = ProfileUiState(
                        nickname = userName,
                        totalEpisodes = entries.size,
                        weekStats = if (entries.isNotEmpty()) listOf(
                            DayStat("Пн", (entries.size * 0.2).toInt().coerceAtLeast(1), 15),
                            DayStat("Вт", (entries.size * 0.3).toInt().coerceAtLeast(1), 25),
                            DayStat("Ср", (entries.size * 0.1).toInt().coerceAtLeast(1), 10),
                            DayStat("Чт", (entries.size * 0.2).toInt().coerceAtLeast(1), 20),
                            DayStat("Пт", (entries.size * 0.2).toInt().coerceAtLeast(1), 30)
                        ) else emptyList()
                    ),
                    onNicknameChange = viewModel::updateUserName
                )
            }
        }
    }
}
