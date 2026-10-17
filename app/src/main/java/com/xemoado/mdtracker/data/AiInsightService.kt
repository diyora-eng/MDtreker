package com.xemoado.mdtracker.data

import java.util.Locale

class AiInsightService {

    /**
     * Генерирует текстовый обзор паттернов на основе агрегированной статистики эпизодов.
     * Содержание и сюжеты фантазий сюда НЕ передаются.
     */
    fun generateInsight(stats: EpisodeStats): String {
        val count = stats.entriesCount
        if (count == 0) {
            return "Пока нет данных для обзора — добавьте хотя бы 3 записи."
        }

        val topTrigger = stats.byTrigger.maxByOrNull { it.value }?.key ?: "—"
        val topTimeBucket = stats.byTimeOfDay.maxByOrNull { it.value }?.key ?: "morning"

        val timeOfDayText = when (topTimeBucket) {
            "morning" -> "утром (05:00–12:00)"
            "day" -> "днём (12:00–18:00)"
            "evening" -> "вечером (18:00–23:00)"
            else -> "ночью"
        }

        val advice = when {
            stats.avgCraving >= 3.5 ->
                "Доминирует фактор сильной тяги. Рекомендуется использовать технику отложенного погружения (Urge-delay) на 10–15 минут."
            stats.avgControl <= 2.5 ->
                "Наблюдается снижение контроля над переключением. Используйте методы физического якорения и тайм-блокинг."
            else ->
                "Обратите внимание на эпизоды, возникающие во время '$topTrigger'. Применяйте заземление при первых признаках импульса."
        }

        return """
            Обзор паттернов за ${stats.periodDays} дней ($count записей):
            • Частый триггер: $topTrigger
            • Наиболее частый период: $timeOfDayText
            • Средняя длительность: ${stats.avgDurationMin} мин.
            • Среднее управление: ${String.format(Locale.getDefault(), "%.1f", stats.avgControl)} / 5
            • Уровень дистресса: ${String.format(Locale.getDefault(), "%.1f", stats.avgDistress)} / 5

            Совет: $advice
        """.trimIndent()
    }
}
