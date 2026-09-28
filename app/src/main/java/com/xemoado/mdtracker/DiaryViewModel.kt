package com.xemoado.mdtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DiaryRepository

    val entries: StateFlow<List<DiaryEntry>>

    init {
        val dao = AppDatabase.getDatabase(application).diaryDao()
        repository = DiaryRepository(dao)
        entries = repository.allEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun saveEntry(trigger: String, analysis: String) {
        viewModelScope.launch {
            repository.insert(DiaryEntry(trigger = trigger, analysis = analysis))
        }
    }
}