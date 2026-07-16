package com.zerosepaisa.liferesetos.feature.journey

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JourneyViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val missionRepository = MissionRepository(
        AppDatabase.getInstance(application).missionDao()
    )

    private val goalRepository = GoalRepository(
        AppDatabase.getInstance(application).goalDao()
    )

    private val _activeMission = MutableStateFlow<Mission?>(null)
    val activeMission: StateFlow<Mission?> = _activeMission.asStateFlow()

    private val _activeGoals = MutableStateFlow<List<Goal>>(emptyList())
    val activeGoals: StateFlow<List<Goal>> = _activeGoals.asStateFlow()

    init {
        viewModelScope.launch {
            missionRepository.getActiveMission().collect {
                _activeMission.value = it
            }
        }

        viewModelScope.launch {
            goalRepository.getActiveGoals().collect {
                _activeGoals.value = it
            }
        }
    }
}
