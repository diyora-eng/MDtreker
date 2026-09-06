package com.xemoado.mdtracker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow("MD Tracker")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun onRegisterClick() {
    }
}