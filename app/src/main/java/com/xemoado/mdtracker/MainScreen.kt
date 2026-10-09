package com.xemoado.mdtracker

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val TRIGGERS_LIST = listOf(
    "Скука",
    "Стресс",
    "Тревога",
    "Засыпание / бессонница",
    "Соц. ситуация",
    "Рутинная задача",
    "Другое"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    viewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    onNavigateToBase: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val entries by viewModel.entries.collectAsState(initial = emptyList())

    var showSuccessMessage by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }
    var aiReportText by remember { mutableStateOf<String?>(null) }
    var isGeneratingAiReport by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            showSuccessMessage = true
            viewModel.clearInputs()
            viewModel.onSaveHandled()
            delay(3000)
            showSuccessMessage = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Дневник потока",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Фиксируйте не сюжет фантазии, а её контур: что предшествовало, сколько длилось, насколько удавалось управлять собой.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Success Notification
        if (showSuccessMessage) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "Запись эпизода успешно сохранена!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // New Episode Entry Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Записать эпизод",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Trigger Chips
                Text(
                    text = "Что предшествовало? (Триггер)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TRIGGERS_LIST.forEach { trigger ->
                        val selected = uiState.selectedTrigger == trigger
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                            modifier = Modifier.clickable { viewModel.onTriggerSelected(trigger) }
                        ) {
                            Text(
                                text = trigger,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Длительность (минут):",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onDurationChanged(uiState.durationMinutes - 5) },
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("-")
                        }
                        Text(
                            text = "${uiState.durationMinutes} мин",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        OutlinedButton(
                            onClick = { viewModel.onDurationChanged(uiState.durationMinutes + 5) },
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("+")
                        }
                    }
                }

                // Slider 1: Control Level
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Управление собой:", style = MaterialTheme.typography.bodySmall)
                        Text("${uiState.controlLevel.toInt()} / 5", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = uiState.controlLevel,
                        onValueChange = viewModel::onControlChanged,
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("непроизвольно", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("осознанный выбор", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                // Slider 2: Craving Level
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Тяга вернуться в фантазию:", style = MaterialTheme.typography.bodySmall)
                        Text("${uiState.cravingLevel.toInt()} / 5", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = uiState.cravingLevel,
                        onValueChange = viewModel::onCravingChanged,
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("нет", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("сильная", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                // Slider 3: Distress Level
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Дистресс / тяжесть:", style = MaterialTheme.typography.bodySmall)
                        Text("${uiState.distressLevel.toInt()} / 5", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = uiState.distressLevel,
                        onValueChange = viewModel::onDistressChanged,
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("нет", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("сильный", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                // Toggle Advanced Details
                TextButton(
                    onClick = { showAdvanced = !showAdvanced },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(if (showAdvanced) "- Скрыть тему и заметку" else "+ Добавить тему и заметку (необязательно)")
                }

                AnimatedVisibility(visible = showAdvanced) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.topicInput,
                            onValueChange = viewModel::onTopicChanged,
                            label = { Text("Тема (одним-двумя словами)") },
                            placeholder = { Text("например: альтернативная карьера") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.stateInput,
                            onValueChange = viewModel::onStateChanged,
                            label = { Text("Короткая заметка") },
                            placeholder = { Text("например: началось за компьютером...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }

                uiState.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { viewModel.saveCurrentEntry() },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        if (uiState.isSaving) "Сохранение..." else "Сохранить эпизод",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Pattern Insights Card (AI Analysis)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Обзор паттернов",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = {
                            isGeneratingAiReport = true
                            val count = entries.size
                            val avgControl = if (count > 0) entries.map { it.controlLevel }.average() else 3.0
                            val avgCraving = if (count > 0) entries.map { it.cravingLevel }.average() else 3.0
                            val avgDistress = if (count > 0) entries.map { it.distressLevel }.average() else 2.0
                            val topTrigger = if (count > 0) entries.groupBy { it.trigger }.maxByOrNull { it.value.size }?.key else "—"

                            aiReportText = "Анализ ${count} эпизодов:\n• Частый триггер: $topTrigger\n• Средний уровень контроля: ${String.format(Locale.getDefault(), "%.1f", avgControl)}/5\n• Тяга к повтору: ${String.format(Locale.getDefault(), "%.1f", avgCraving)}/5\n• Уровень дистресса: ${String.format(Locale.getDefault(), "%.1f", avgDistress)}/5\n\nСовет: Обратите внимание на эпизоды, связанные с '$topTrigger'. Используйте технику осознанного замедления при первых признаках погружения."
                            isGeneratingAiReport = false
                        },
                        enabled = entries.isNotEmpty() && !isGeneratingAiReport
                    ) {
                        Text("Собрать обзор")
                    }
                }

                Text(
                    text = "ИИ смотрит на статистику эпизодов — триггеры, длительность, контроль и дистресс.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                aiReportText?.let { report ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = report,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Advisory check if avg distress high
                if (entries.isNotEmpty()) {
                    val avgDistress = entries.map { it.distressLevel }.average()
                    val avgControl = entries.map { it.controlLevel }.average()
                    if (avgDistress >= 3.5 || avgControl <= 2.0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Предупреждение",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "Уровень дистресса или потери контроля в последних записях повышен. Стоит обсудить это с психотерапевтом.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Entries Preview
        if (entries.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Последние эпизоды",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val sdf = remember { SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()) }

                    entries.take(3).forEach { entry ->
                        val badgeColor = when {
                            entry.distressLevel >= 4 -> Color(0xFFE2707A)
                            entry.cravingLevel >= 4 -> Color(0xFF7C8CF5)
                            entry.controlLevel <= 2 -> Color(0xFFE8A857)
                            else -> Color(0xFF6FC9A6)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(badgeColor, shape = CircleShape)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = entry.trigger,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = sdf.format(Date(entry.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = "${entry.durationMinutes} мин · контроль ${entry.controlLevel}/5 · дистресс ${entry.distressLevel}/5" +
                                            if (entry.topic.isNotBlank()) " · «${entry.topic}»" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
