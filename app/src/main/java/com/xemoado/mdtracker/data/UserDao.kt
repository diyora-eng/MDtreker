package com.xemoado.mdtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users WHERE id = :id")
    suspend fun exists(id: String): Int
}
