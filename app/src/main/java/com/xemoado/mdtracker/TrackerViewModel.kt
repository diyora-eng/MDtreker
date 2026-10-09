package com.xemoado.mdtracker

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrackerUiState(
    val selectedTrigger: String = "Скука",
    val durationMinutes: Int = 15,
    val controlLevel: Float = 3f,
    val cravingLevel: Float = 3f,
    val distressLevel: Float = 2f,
    val topicInput: String = "",
    val stateInput: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TrackerDatabase.getInstance(application).trackerDao()
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private val _userName = MutableStateFlow(
        prefs.getString("user_nickname", "Пользователь") ?: "Пользователь"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    val entries = dao.getAllEntries()

    fun updateUserName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotEmpty()) {
            _userName.value = trimmed
            prefs.edit().putString("user_nickname", trimmed).apply()
        }
    }

    fun onTriggerSelected(trigger: String) {
        _uiState.value = _uiState.value.copy(selectedTrigger = trigger)
    }

    fun onDurationChanged(minutes: Int) {
        _uiState.value = _uiState.value.copy(durationMinutes = minutes.coerceIn(1, 600))
    }

    fun onControlChanged(level: Float) {
        _uiState.value = _uiState.value.copy(controlLevel = level)
    }

    fun onCravingChanged(level: Float) {
        _uiState.value = _uiState.value.copy(cravingLevel = level)
    }

    fun onDistressChanged(level: Float) {
        _uiState.value = _uiState.value.copy(distressLevel = level)
    }

    fun onTopicChanged(value: String) {
        _uiState.value = _uiState.value.copy(topicInput = value)
    }

    fun onStateChanged(value: String) {
        _uiState.value = _uiState.value.copy(stateInput = value)
    }

    fun saveCurrentEntry(onSuccess: () -> Unit = {}) {
        val state = _uiState.value
        addFullEntry(
            trigger = state.selectedTrigger,
            durationMinutes = state.durationMinutes,
            controlLevel = state.controlLevel.toInt(),
            cravingLevel = state.cravingLevel.toInt(),
            distressLevel = state.distressLevel.toInt(),
            topic = state.topicInput,
            stateDescription = state.stateInput,
            onSuccess = onSuccess
        )
    }

    fun addFullEntry(
        trigger: String,
        durationMinutes: Int,
        controlLevel: Int,
        cravingLevel: Int,
        distressLevel: Int,
        topic: String,
        stateDescription: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                dao.insert(
                    TrackerEntry(
                        trigger = if (trigger.isBlank()) "Не указано" else trigger.trim(),
                        durationMinutes = durationMinutes.coerceIn(1, 600),
                        controlLevel = controlLevel.coerceIn(1, 5),
                        cravingLevel = cravingLevel.coerceIn(1, 5),
                        distressLevel = distressLevel.coerceIn(1, 5),
                        topic = topic.trim(),
                        stateDescription = stateDescription.trim()
                    )
                )
                _uiState.value = TrackerUiState(saveSuccess = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Не удалось сохранить запись")
            }
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    fun clearInputs() {
        _uiState.value = TrackerUiState()
    }

    fun onSaveHandled() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
