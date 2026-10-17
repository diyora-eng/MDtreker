package com.xemoado.mdtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Insert
    suspend fun insert(e: EpisodeEntity)

    // userId в условии — защита от удаления чужой записи при нескольких аккаунтах
    @Query("DELETE FROM episodes WHERE id = :id AND userId = :userId")
    suspend fun delete(id: String, userId: String)

    // Flow: любая вставка/удаление автоматически присылает новый список
    @Query(
        """SELECT * FROM episodes
           WHERE userId = :userId AND startedAt >= :from
           ORDER BY startedAt DESC"""
    )
    fun observe(userId: String, from: Long): Flow<List<EpisodeEntity>>

    // для будущей синхронизации
    @Query("SELECT * FROM episodes WHERE userId = :userId AND synced = 0")
    suspend fun unsynced(userId: String): List<EpisodeEntity>

    // удалить все данные пользователя (кнопка «стереть всё» в настройках)
    @Query("DELETE FROM episodes WHERE userId = :userId")
    suspend fun deleteAll(userId: String)
}
