package com.xemoado.mdtracker

import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val dao: DiaryDao) {
    val allEntries: Flow<List<DiaryEntry>> = dao.getAll()

    suspend fun insert(entry: DiaryEntry) {
        dao.insert(entry)
    }
}