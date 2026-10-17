package com.xemoado.mdtracker.data

enum class PointKind { DISTRESS, CRAVING, LOST_CONTROL, CALM }

/** Одна точка на «реке»: строка = день, x = время суток, радиус = длительность. */
data class RiverPoint(
    val episodeId: String,
    val dayIndex: Int, // 0 = самый старый день периода, days-1 = сегодня
    val xFraction: Float, // 0f..1f — время суток
    val radius: Float, // 5..16
    val kind: PointKind
)

/** Черновик из формы быстрой записи. */
data class EpisodeDraft(
    val triggerType: String?,
    val durationMin: Int,
    val control: Int,
    val craving: Int,
    val distress: Int,
    val topic: String = "",
    val note: String = ""
)

data class EpisodeStats(
    val periodDays: Int,
    val entriesCount: Int,
    val byTrigger: Map<String, Int>,
    val byTimeOfDay: Map<String, Int>,
    val avgDurationMin: Int,
    val avgControl: Double,
    val avgCraving: Double,
    val avgDistress: Double
)
