package com.xemoado.mdtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // UUID; позже можно заменить на uid из Firebase Auth
    val createdAt: Long = System.currentTimeMillis()
)
