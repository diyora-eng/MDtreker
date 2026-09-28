package com.xemoado.mdtracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    viewModel: DiaryViewModel = viewModel(),
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var triggerText by remember { mutableStateOf("") }
    var analysisText by remember { mutableStateOf("") }
    val entries by viewModel.entries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MD Tracker — Дневник триггеров") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.List, contentDescription = "История записей")
                    }
                },
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
            Text(text = "Что вызвало навязчивые грёзы?", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = triggerText,
                onValueChange = { triggerText = it },
                label = { Text("Опишите триггер (музыка, фильм, скука...)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text(text = "Анализ состояния", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = analysisText,
                onValueChange = { analysisText = it },
                label = { Text("Какие эмоции вы испытывали? Как долго длились грёзы?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Button(
                onClick = {
                    if (triggerText.isNotBlank()) {
                        viewModel.saveEntry(triggerText, analysisText)
                        triggerText = ""
                        analysisText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить запись")
            }

            Text(text = "Сохранённые записи", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(entries) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = entry.trigger, style = MaterialTheme.typography.bodyLarge)
                            Text(text = entry.analysis, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}