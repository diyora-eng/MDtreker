package com.xemoado.mdtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trigger: String,
    val analysis: String,
    val createdAt: Long = System.currentTimeMillis()
)