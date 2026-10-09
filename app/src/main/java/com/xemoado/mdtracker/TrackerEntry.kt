package com.xemoado.mdtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracker_dreams")
data class TrackerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trigger: String,
    val durationMinutes: Int = 15,
    val controlLevel: Int = 3,
    val cravingLevel: Int = 3,
    val distressLevel: Int = 2,
    val topic: String = "",
    val stateDescription: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
