package com.xemoado.mdtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackerEntry::class], version = 1)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao

    companion object {
        @Volatile private var INSTANCE: TrackerDatabase? = null

        fun getInstance(context: Context): TrackerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDatabase::class.java,
                    "mdtracker.db"
                ).build().also { INSTANCE = it }
            }
    }
}
