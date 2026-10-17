@file:Suppress("NewApi")

package com.xemoado.mdtracker.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private val zone: ZoneId get() = ZoneId.systemDefault()

fun startOfDay(daysBack: Int): Long =
    LocalDate.now().minusDays(daysBack.toLong()).atStartOfDay(zone).toInstant().toEpochMilli()

fun EpisodeDraft.toEntity(userId: String): EpisodeEntity = EpisodeEntity(
    userId = userId,
    startedAt = System.currentTimeMillis(),
    durationMin = durationMin.coerceIn(1, 600),
    triggerType = triggerType ?: "Не указано",
    control = control.coerceIn(1, 5),
    craving = craving.coerceIn(1, 5),
    distress = distress.coerceIn(1, 5),
    topic = topic.trim().take(40).ifBlank { null },
    note = note.trim().take(500).ifBlank { null }
)

fun EpisodeEntity.kind(): PointKind = when {
    distress >= 4 -> PointKind.DISTRESS
    craving >= 4 -> PointKind.CRAVING
    control <= 2 -> PointKind.LOST_CONTROL
    else -> PointKind.CALM
}

fun EpisodeEntity.toPoint(days: Int): RiverPoint {
    val dt = Instant.ofEpochMilli(startedAt).atZone(zone)
    val ago = ChronoUnit.DAYS.between(dt.toLocalDate(), LocalDate.now()).toInt()
    return RiverPoint(
        episodeId = id,
        dayIndex = (days - 1) - ago,
        xFraction = (dt.hour + dt.minute / 60f) / 24f,
        radius = (5f + durationMin / 10f).coerceIn(5f, 16f),
        kind = kind()
    )
}

/** Агрегированная статистика для ИИ-обзора. topic и note сюда НЕ попадают. */
fun summarize(list: List<EpisodeEntity>, periodDays: Int): EpisodeStats {
    val byTrigger = list.groupingBy { it.triggerType }.eachCount()
    val buckets = mutableMapOf("morning" to 0, "day" to 0, "evening" to 0, "night" to 0)
    list.forEach { e ->
        val h = Instant.ofEpochMilli(e.startedAt).atZone(zone).hour
        val key = when (h) {
            in 5..11 -> "morning"
            in 12..17 -> "day"
            in 18..22 -> "evening"
            else -> "night"
        }
        buckets[key] = buckets.getValue(key) + 1
    }
    val n = list.size.coerceAtLeast(1)
    fun avg(sel: (EpisodeEntity) -> Int) = Math.round(list.sumOf(sel).toDouble() / n * 10) / 10.0
    return EpisodeStats(
        periodDays = periodDays,
        entriesCount = list.size,
        byTrigger = byTrigger,
        byTimeOfDay = buckets,
        avgDurationMin = Math.round(list.sumOf { it.durationMin }.toDouble() / n).toInt(),
        avgControl = avg { it.control },
        avgCraving = avg { it.craving },
        avgDistress = avg { it.distress }
    )
}

/** Баннер «обсудите с психологом»: среди последних 7 записей, не менее 3. */
fun shouldShowAdvisory(list: List<EpisodeEntity>): Boolean {
    val recent = list.take(7)
    if (recent.size < 3) return false
    val avgDistress = recent.map { it.distress }.average()
    val avgControl = recent.map { it.control }.average()
    return avgDistress >= 3.5 || avgControl <= 2.0
}
