package com.xemoado.mdtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // удалили пользователя — удалились его эпизоды
        )
    ],
    indices = [Index(value = ["userId", "startedAt"])] // быстрая выборка «записи пользователя за N дней»
)
data class EpisodeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val startedAt: Long, // epoch millis
    val durationMin: Int, // 1..600
    val triggerType: String, // «Скука», «Стресс», ...
    val control: Int, // 1..5 (1 — затянуло непроизвольно, 5 — осознанный выбор)
    val craving: Int, // 1..5
    val distress: Int, // 1..5
    val topic: String? = null, // ярлык в 1–2 слова, необязательно
    val note: String? = null, // короткая заметка, необязательно
    val updatedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
