package com.xemoado.mdtracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EpisodeRepository(private val dao: EpisodeDao) {

    fun episodes(userId: String, days: Int): Flow<List<EpisodeEntity>> =
        dao.observe(userId, startOfDay(days - 1))

    fun riverPoints(userId: String, days: Int): Flow<List<RiverPoint>> =
        episodes(userId, days).map { list ->
            list.map { it.toPoint(days) }.filter { it.dayIndex in 0 until days }
        }

    suspend fun save(e: EpisodeEntity) = dao.insert(e)
    suspend fun delete(id: String, userId: String) = dao.delete(id, userId)
    suspend fun deleteAll(userId: String) = dao.deleteAll(userId)
}
