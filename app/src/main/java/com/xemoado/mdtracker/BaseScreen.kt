package com.xemoado.mdtracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    viewModel: DiaryViewModel = viewModel(),
    onNavigateToHistory: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
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
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "История записей")
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
