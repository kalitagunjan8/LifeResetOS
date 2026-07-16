package com.zerosepaisa.liferesetos.feature.mission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MissionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = MissionRepository(
        AppDatabase.getInstance(application).missionDao()
    )

    private val _uiState = MutableStateFlow(MissionUiState())
    val uiState: StateFlow<MissionUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateStatement(statement: String) {
        _uiState.value = _uiState.value.copy(statement = statement)
    }

    fun updateWhy(why: String) {
        _uiState.value = _uiState.value.copy(why = why)
    }

    fun saveMission(onSaved: () -> Unit) {

        val state = _uiState.value

        if (
            state.title.isBlank() ||
            state.statement.isBlank() ||
            state.why.isBlank()
        ) {
            return
        }

        viewModelScope.launch {

            repository.saveMission(
                Mission(
                    title = state.title.trim(),
                    statement = state.statement.trim(),
                    why = state.why.trim(),
                    isActive = true
                )
            )

            onSaved()
        }
    }
}