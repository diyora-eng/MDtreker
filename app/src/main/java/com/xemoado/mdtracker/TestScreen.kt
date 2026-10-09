package com.xemoado.mdtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

data class Question(
    val kicker: String,
    val text: String,
    val opts: List<String>,
    val type: String, // "scored", "screening", "profile"
    val factor: String?, // "yearning", "kinesthesia", "impairment"
    val tagLabel: String,
    val tagColor: Color
)

val QUESTIONS_LIST = listOf(
    // Yearning
    Question(
        kicker = "MDS · частота",
        text = "Как часто в течение дня у вас возникают яркие, затягивающие мечтания?",
        opts = listOf("Почти никогда", "Раз-два в день", "Несколько раз в день", "Очень часто, почти постоянный фон", "Занимают бóльшую часть дня"),
        type = "scored", factor = "yearning", tagLabel = "Тоска и влечение", tagColor = Color(0xFF8574B3)
    ),
    Question(
        kicker = "MDS · тяга к мечтанию",
        text = "Насколько сильно вас тянет вернуться к мечтанию, даже когда вы пытаетесь заняться чем-то другим?",
        opts = listOf("Совсем не тянет", "Лёгкое желание", "Заметная тяга", "Сильная тяга, трудно игнорировать", "Непреодолимая тяга"),
        type = "scored", factor = "yearning", tagLabel = "Тоска и влечение", tagColor = Color(0xFF8574B3)
    ),
    Question(
        kicker = "MDS · ощущение присутствия",
        text = "Насколько яркими и «реальными» ощущаются сцены (образы, диалоги, эмоции)?",
        opts = listOf("Смутные, обрывочные", "Умеренно живые", "Довольно яркие", "Очень яркие, почти как кино", "Кажутся почти настоящими"),
        type = "scored", factor = "yearning", tagLabel = "Тоска и влечение", tagColor = Color(0xFF8574B3)
    ),

    // Kinesthesia
    Question(
        kicker = "MDS · движения",
        text = "Сопровождается ли мечтание повторяющимися физическими движениями — хождением, покачиванием, жестикуляцией, подбрасыванием предмета?",
        opts = listOf("Никогда", "Иногда, случайно", "Часто, это помогает погрузиться", "Почти всегда — это часть ритуала", "Не могу мечтать без движения"),
        type = "scored", factor = "kinesthesia", tagLabel = "Кинестезия", tagColor = Color(0xFFC98A3E)
    ),
    Question(
        kicker = "MDS · длительность",
        text = "Сколько обычно длится один эпизод мечтания?",
        opts = listOf("Меньше минуты", "Несколько минут", "10–30 минут", "30–60 минут", "Больше часа"),
        type = "scored", factor = "kinesthesia", tagLabel = "Кинестезия", tagColor = Color(0xFFC98A3E)
    ),

    // Impairment
    Question(
        kicker = "MDS · потеря контроля",
        text = "Насколько трудно остановить мечтание, когда вы понимаете, что пора?",
        opts = listOf("Легко переключаюсь", "Немного усилий", "Заметно трудно", "Почти невозможно сходу", "Не могу остановиться сам(а)"),
        type = "scored", factor = "impairment", tagLabel = "Нарушение", tagColor = Color(0xFFC25C4F)
    ),
    Question(
        kicker = "MDS · работа/учёба",
        text = "Мешает ли мечтание концентрироваться на работе, учёбе или задачах?",
        opts = listOf("Не мешает", "Изредка отвлекает", "Заметно снижает продуктивность", "Часто срываю дела", "Почти не могу работать"),
        type = "scored", factor = "impairment", tagLabel = "Нарушение", tagColor = Color(0xFFC25C4F)
    ),
    Question(
        kicker = "MDS · сон и отдых",
        text = "Влияет ли мечтание на засыпание или качество отдыха?",
        opts = listOf("Не влияет", "Иногда задерживает засыпание", "Регулярно ворую время у сна", "Сильно нарушает режим сна", "Сон почти разрушен"),
        type = "scored", factor = "impairment", tagLabel = "Нарушение", tagColor = Color(0xFFC25C4F)
    ),
    Question(
        kicker = "MDS · дистресс",
        text = "Насколько вас беспокоит сам факт того, что вы так много мечтаете?",
        opts = listOf("Не беспокоит", "Слегка", "Умеренно", "Сильно", "Очень сильно, стыдно/тревожно"),
        type = "scored", factor = "impairment", tagLabel = "Нарушение", tagColor = Color(0xFFC25C4F)
    ),

    // Screening
    Question(
        kicker = "Скрининг · навязчивости",
        text = "Бывают ли у вас навязчивые мысли, ритуалы или проверки, не связанные с мечтанием — например, потребность делать что-то определённым образом или по кругу?",
        opts = listOf("Нет", "Изредка, не напрягает", "Периодически, немного мешает", "Часто, заметно напрягает", "Да, регулярно и это тяжело"),
        type = "screening", factor = null, tagLabel = "Скрининг", tagColor = Color(0xFF5C8A6F)
    ),
    Question(
        kicker = "Скрининг · поглощённость",
        text = "Замечаете ли вы, что «выпадаете» из происходящего вокруг — не только во время мечтаний, но и, например, за фильмом или за рулём?",
        opts = listOf("Нет", "Очень редко", "Иногда", "Довольно часто", "Постоянно, это пугает"),
        type = "screening", factor = null, tagLabel = "Скрининг", tagColor = Color(0xFF5C8A6F)
    ),
    Question(
        kicker = "Скрининг · внимание",
        text = "Трудно ли вам в целом удерживать концентрацию — не только из-за мечтаний, а в разных ситуациях?",
        opts = listOf("Не трудно", "Немного", "Заметно", "Сильно", "Очень сильно, мешает жить"),
        type = "screening", factor = null, tagLabel = "Скрининг", tagColor = Color(0xFF5C8A6F)
    ),

    // Profile
    Question(
        kicker = "Профиль · время",
        text = "Когда мечтания чаще всего застигают вас врасплох?",
        opts = listOf("Утром / по дороге", "Днём, во время работы или учёбы", "Вечером, перед сном", "Ночью, когда не спится"),
        type = "profile", factor = null, tagLabel = "Профиль графика", tagColor = Color(0xFF3D4F78)
    ),
    Question(
        kicker = "Профиль · триггер",
        text = "Мечтание чаще возникает как способ уйти от стресса, скуки или сильных эмоций?",
        opts = listOf("Почти нет такой связи", "Изредка", "Часто", "Почти всегда так"),
        type = "profile", factor = null, tagLabel = "Профиль графика", tagColor = Color(0xFF3D4F78)
    ),
    Question(
        kicker = "Профиль · ресурс",
        text = "Сколько времени в день вы реально готовы уделять упражнениям плана?",
        opts = listOf("3–5 минут", "10–15 минут", "20–30 минут", "Готов(а) гибко подстраиваться"),
        type = "profile", factor = null, tagLabel = "Профиль графика", tagColor = Color(0xFF3D4F78)
    )
)

@Composable
fun TestScreen() {
    var stage by remember { mutableIntStateOf(0) } // 0: Start, 1: Quiz, 2: Result
    var currentIdx by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(IntArray(QUESTIONS_LIST.size) { -1 }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (stage == 0) {
            // Hero Start Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Вводный тест MDS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Тест построен на трёхфакторной модели MDS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "15 коротких вопросов: 9 формируют основной балл, 3 — скрининг сопутствующих паттернов, 3 — персональный график. Займёт 3–4 минуты.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            currentIdx = 0
                            answers = IntArray(QUESTIONS_LIST.size) { -1 }
                            stage = 1
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Начать тест →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (stage == 1) {
            // Quiz Card
            val q = QUESTIONS_LIST[currentIdx]
            val progress = (currentIdx + 1).toFloat() / QUESTIONS_LIST.size

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = q.tagLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = q.tagColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${currentIdx + 1} / ${QUESTIONS_LIST.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = q.kicker,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = q.text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    q.opts.forEachIndexed { optIdx, optLabel ->
                        val selected = answers[currentIdx] == optIdx
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (selected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newAns = answers.copyOf()
                                    newAns[currentIdx] = optIdx
                                    answers = newAns
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .border(
                                            2.dp,
                                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = optLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentIdx > 0) {
                            OutlinedButton(onClick = { currentIdx-- }) {
                                Text("Назад")
                            }
                        } else {
                            Spacer(modifier = Modifier.size(1.dp))
                        }

                        Button(
                            onClick = {
                                if (currentIdx < QUESTIONS_LIST.size - 1) {
                                    currentIdx++
                                } else {
                                    stage = 2
                                }
                            },
                            enabled = answers[currentIdx] != -1
                        ) {
                            Text(if (currentIdx == QUESTIONS_LIST.size - 1) "Получить результат" else "Далее")
                        }
                    }
                }
            }
        } else {
            // Result Stage
            val yAns = listOf(answers[0], answers[1], answers[2]).filter { it != -1 }
            val kAns = listOf(answers[3], answers[4]).filter { it != -1 }
            val iAns = listOf(answers[5], answers[6], answers[7], answers[8]).filter { it != -1 }

            val yScore = if (yAns.isNotEmpty()) yAns.average() / 4.0 * 100 else 0.0
            val kScore = if (kAns.isNotEmpty()) kAns.average() / 4.0 * 100 else 0.0
            val iScore = if (iAns.isNotEmpty()) iAns.average() / 4.0 * 100 else 0.0

            val totalScore = Math.round((yScore + kScore + iScore) / 3.0).toInt()

            val (levelTitle, levelColor) = when {
                totalScore < 25 -> "Лёгкий уровень" to Color(0xFF5C8A6F)
                totalScore < 50 -> "Умеренный уровень" to Color(0xFFC98A3E)
                totalScore < 75 -> "Выраженный уровень" to Color(0xFFB06A2E)
                else -> "Высокий уровень" to Color(0xFFC25C4F)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Результат теста MDS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$totalScore / 100 балов",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = levelTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                }
            }

            // Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Разбивка по факторам",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    FactorRow("Тоска / влечение", yScore.toInt(), Color(0xFF8574B3))
                    FactorRow("Кинестезия", kScore.toInt(), Color(0xFFC98A3E))
                    FactorRow("Нарушение", iScore.toInt(), Color(0xFFC25C4F))
                }
            }

            // Personal Recommendations Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Персональные рекомендации",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val dominant = listOf("Тоска" to yScore, "Кинестезия" to kScore, "Нарушение" to iScore).maxByOrNull { it.second }?.first ?: "Тоска"

                    Text(
                        text = "• Доминирующий фактор: $dominant.\n• Рекомендуется ведение 'Дневника потока' 2-3 раза в день для отслеживания триггеров и уровня контроля.\n• Применяйте техники осознанного якорения при появлении тяги.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            Button(
                onClick = { stage = 0 },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Пройти тест заново")
            }
        }
    }
}

@Composable
fun FactorRow(label: String, value: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text("$value%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color
        )
    }
}
