package com.xemoado.mdtracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Insert
    suspend fun insert(entry: TrackerEntry)

    @Query("DELETE FROM tracker_dreams WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM tracker_dreams")
    suspend fun deleteAll()

    @Query("SELECT * FROM tracker_dreams ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<TrackerEntry>>
}
