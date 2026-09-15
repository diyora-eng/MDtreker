package com.xemoado.mdtracker

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    viewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val entries by viewModel.entries.collectAsState(initial = emptyList())

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onSaveHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MD Tracker — Дневник триггеров") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Что вызвало навязчивые грёзы?",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = uiState.triggerInput,
                onValueChange = viewModel::onTriggerChanged,
                label = { Text("Опишите триггер (музыка, фильм, скука...)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )


            Text(
                text = "Анализ состояния",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = uiState.stateInput,
                onValueChange = viewModel::onStateChanged,
                label = { Text("Какие эмоции вы испытывали? Как долго длились грёзы?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onRegisterClick() },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isSaving) "Сохранение..." else "Сохранить запись")
            }

            HorizontalDivider()

            Text(
                text = "История записей",
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(entries) { entry ->
                    EntryCard(entry)
                }
            }
        }
    }
}

@Composable
fun EntryCard(entry: TrackerEntry) {
    val sdf = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val dateString = sdf.format(Date(entry.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = dateString,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            if (entry.trigger.isNotEmpty()) {
                Text(
                    text = "Триггер: ${entry.trigger}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (entry.stateDescription.isNotEmpty()) {
                Text(
                    text = "Состояние: ${entry.stateDescription}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}