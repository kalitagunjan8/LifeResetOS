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
import com.zerosepaisa.liferesetos.data.repository.HabitCompletionRepository
import kotlinx.coroutines.flow.map
import com.zerosepaisa.liferesetos.streaks.HabitStreak
import com.zerosepaisa.liferesetos.streaks.HabitStreakEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalCoroutinesApi::class)
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

    private val habitCompletionRepository = HabitCompletionRepository(
        AppDatabase.getInstance(application).habitCompletionDao()
    )

    private val habitStreakEngine = HabitStreakEngine(habitCompletionRepository)

    private val _activeMission = MutableStateFlow<Mission?>(null)
    val activeMission: StateFlow<Mission?> = _activeMission.asStateFlow()

    private val _activeGoals = MutableStateFlow<List<Goal>>(emptyList())
    val activeGoals: StateFlow<List<Goal>> = _activeGoals.asStateFlow()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _todaysCompletedHabitIds = MutableStateFlow<Set<Long>>(emptySet())
    val todaysCompletedHabitIds: StateFlow<Set<Long>> = _todaysCompletedHabitIds.asStateFlow()

    private val _habitStreaks = MutableStateFlow<Map<Long, HabitStreak>>(emptyMap())
    val habitStreaks: StateFlow<Map<Long, HabitStreak>> = _habitStreaks.asStateFlow()

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

        viewModelScope.launch {
            habitCompletionRepository.getTodaysCompletions()
                .map { completions -> completions.map { it.habitId }.toSet() }
                .collect {
                    _todaysCompletedHabitIds.value = it
                }
        }

        viewModelScope.launch {
            habitRepository.getAllHabits()
                .flatMapLatest { habits ->
                    if (habits.isEmpty()) {
                        flowOf(emptyMap())
                    } else {
                        val perHabitFlows = habits.map { habit ->
                            habitStreakEngine.observeStreakForHabit(habit.id)
                                .map { streak -> habit.id to streak }
                        }
                        combine(perHabitFlows) { pairs -> pairs.toMap() }
                    }
                }
                .collect {
                    _habitStreaks.value = it
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

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            habitCompletionRepository.toggleTodaysCompletion(habit.id)
        }
    }
}