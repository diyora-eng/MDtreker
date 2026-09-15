package com.xemoado.mdtracker
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracker_entries")
data class TrackerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trigger: String,
    val stateDescription: String,
    val timestamp: Long = System.currentTimeMillis()
)