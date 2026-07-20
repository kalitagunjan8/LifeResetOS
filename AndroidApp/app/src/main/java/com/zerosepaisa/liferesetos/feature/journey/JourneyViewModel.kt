package com.zerosepaisa.liferesetos.feature.journey

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Habit
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.HabitRepository
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

    private val habitRepository = HabitRepository(
        AppDatabase.getInstance(application).habitDao()
    )

    private val _activeMission = MutableStateFlow<Mission?>(null)
    val activeMission: StateFlow<Mission?> = _activeMission.asStateFlow()

    private val _activeGoals = MutableStateFlow<List<Goal>>(emptyList())
    val activeGoals: StateFlow<List<Goal>> = _activeGoals.asStateFlow()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

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

        viewModelScope.launch {
            habitRepository.getAllHabits().collect {
                _habits.value = it
            }
        }
    }

    fun createHabit(title: String, description: String) {
        if (title.isBlank()) return

        viewModelScope.launch {
            habitRepository.saveHabit(
                Habit(
                    title = title,
                    description = description
                )
            )
        }
    }

    fun updateHabit(
        original: Habit,
        title: String,
        description: String,
        isActive: Boolean
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            habitRepository.updateHabit(
                original.copy(
                    title = title,
                    description = description,
                    isActive = isActive
                )
            )
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }
}