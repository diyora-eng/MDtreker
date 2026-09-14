package com.xemoado.mdtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrackerUiState(
    val triggerInput: String = "",
    val stateInput: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TrackerDatabase.getInstance(application).trackerDao()

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    val entries = dao.getAllEntries()

    fun onTriggerChanged(value: String) {
        _uiState.value = _uiState.value.copy(triggerInput = value, error = null)
    }

    fun onStateChanged(value: String) {
        _uiState.value = _uiState.value.copy(stateInput = value, error = null)
    }

    fun onRegisterClick() {
        val trigger = _uiState.value.triggerInput.trim()
        val state = _uiState.value.stateInput.trim()

        if (trigger.isEmpty() && state.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Заполните хотя бы одно поле")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true)

        viewModelScope.launch {
            try {
                dao.insert(TrackerEntry(trigger = trigger, stateDescription = state))
                _uiState.value = TrackerUiState(saveSuccess = true) // очищаем поля
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Не удалось сохранить запись")
            }
        }
    }

    fun onSaveHandled() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}