package com.xemoado.mdtracker
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun MainScreen(
    viewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    onNavigateToBase: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp)
    ) {
        Column {
            Text(
                text = "Дневник триггеров",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = uiState.triggerInput,
                onValueChange = viewModel::onTriggerChanged,
                label = { Text("Что вызвало навязчивые грёзы?") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.stateInput,
                onValueChange = viewModel::onStateChanged,
                label = { Text("Анализ состояния") },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onRegisterClick() },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(if (uiState.isSaving) "Сохранение..." else "Сохранить запись")
            }
        }
    }
}



@Composable
fun TrackerScreen(viewModel: TrackerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onSaveHandled()
        }
    }

    Column {
        OutlinedTextField(
            value = uiState.triggerInput,
            onValueChange = viewModel::onTriggerChanged,
            label = { Text("Что вызвало навязчивые грёзы?") }
        )
        OutlinedTextField(
            value = uiState.stateInput,
            onValueChange = viewModel::onStateChanged,
            label = { Text("Анализ состояния") }
        )
        uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = { viewModel.onRegisterClick() },
            enabled = !uiState.isSaving
        ) {
            Text(if (uiState.isSaving) "Сохранение..." else "Сохранить запись")
        }
    }
}