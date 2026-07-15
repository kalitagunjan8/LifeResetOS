package com.zerosepaisa.liferesetos.feature.mission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MissionViewModel : ViewModel() {

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
}