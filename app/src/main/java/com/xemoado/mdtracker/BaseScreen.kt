package com.xemoado.mdtracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen() {

    var triggerText by remember { mutableStateOf("") }
    var analysisText by remember { mutableStateOf("") }

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
                value = triggerText,
                onValueChange = { triggerText = it },
                label = { Text("Опишите триггер (музыка, фильм, скука...)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )


            Text(
                text = "Анализ состояния",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = analysisText,
                onValueChange = { analysisText = it },
                label = { Text("Какие эмоции вы испытывали? Как долго длились грёзы?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))


            Button(
                onClick = {
                    triggerText = ""
                    analysisText = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить запись")
            }
        }
    }
}