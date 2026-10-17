package com.xemoado.mdtracker

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xemoado.mdtracker.data.AppDatabase
import com.xemoado.mdtracker.data.EpisodeDraft
import com.xemoado.mdtracker.data.EpisodeEntity
import com.xemoado.mdtracker.data.EpisodeRepository
import com.xemoado.mdtracker.data.EpisodeStats
import com.xemoado.mdtracker.data.RiverPoint
import com.xemoado.mdtracker.data.SessionStore
import com.xemoado.mdtracker.data.shouldShowAdvisory
import com.xemoado.mdtracker.data.summarize
import com.xemoado.mdtracker.data.toEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val repo = EpisodeRepository(db.episodeDao())
    private val session = SessionStore(app, db.userDao())

    private val prefs = app.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private val _userName = MutableStateFlow(
        prefs.getString("user_nickname", "Пользователь") ?: "Пользователь"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val range = MutableStateFlow(7)
    val currentRange: StateFlow<Int> = range

    private val scope = session.userId.combine(range) { uid, r -> uid to r }

    /** Список эпизодов пользователя. */
    val episodes: StateFlow<List<EpisodeEntity>> = scope
        .flatMapLatest { (uid, r) -> repo.episodes(uid, r) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Точки реки для отображения графика. */
    val river: StateFlow<List<RiverPoint>> = scope
        .flatMapLatest { (uid, r) -> repo.riverPoints(uid, r) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Показывать ли баннер-предупреждение. */
    val advisory: StateFlow<Boolean> = episodes
        .map { shouldShowAdvisory(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /** Кнопка «Собрать обзор» активна от 3 записей. */
    val insightEnabled: StateFlow<Boolean> = episodes
        .map { it.size >= 3 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            session.ensureUser()
        }
    }

    fun setRange(days: Int) {
        range.value = days
    }

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
                val draft = EpisodeDraft(
                    triggerType = if (trigger.isBlank()) "Не указано" else trigger.trim(),
                    durationMin = durationMinutes.coerceIn(1, 600),
                    control = controlLevel.coerceIn(1, 5),
                    craving = cravingLevel.coerceIn(1, 5),
                    distress = distressLevel.coerceIn(1, 5),
                    topic = topic,
                    note = stateDescription
                )
                val uid = session.userId.first()
                repo.save(draft.toEntity(uid))
                _uiState.value = TrackerUiState(saveSuccess = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Не удалось сохранить запись")
            }
        }
    }

    fun save(draft: EpisodeDraft) = viewModelScope.launch {
        val uid = session.userId.first()
        repo.save(draft.toEntity(uid))
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            repo.delete(id, session.userId.first())
        }
    }

    fun eraseAll() = viewModelScope.launch {
        repo.deleteAll(session.userId.first())
    }

    fun statsForInsight(): EpisodeStats =
        summarize(episodes.value.take(30), range.value)

    fun clearInputs() {
        _uiState.value = TrackerUiState()
    }

    fun onSaveHandled() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
