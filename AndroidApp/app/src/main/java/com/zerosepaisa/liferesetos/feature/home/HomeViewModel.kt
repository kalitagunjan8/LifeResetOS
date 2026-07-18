package com.zerosepaisa.liferesetos.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.progress.GlobalProgressSnapshot
import com.zerosepaisa.liferesetos.progress.ProgressEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val missionRepository = MissionRepository(
        AppDatabase.getInstance(application).missionDao()
    )

    private val goalRepository = GoalRepository(
        AppDatabase.getInstance(application).goalDao()
    )

    private val taskRepository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    private val focusSessionRepository = FocusSessionRepository(
        AppDatabase.getInstance(application).focusSessionDao()
    )

    /**
     * Built from the same Repository instances above, per ADR-013 — the
     * ViewModel is the coordinator, owning both the Repositories and the
     * domain service, rather than ProgressEngine replacing this layer.
     */
    private val progressEngine = ProgressEngine(
        missionRepository = missionRepository,
        goalRepository = goalRepository,
        taskRepository = taskRepository,
        focusSessionRepository = focusSessionRepository
    )

    private val _activeMission = MutableStateFlow<Mission?>(null)
    val activeMission: StateFlow<Mission?> = _activeMission.asStateFlow()

    private val _activeGoals = MutableStateFlow<List<Goal>>(emptyList())
    val activeGoals: StateFlow<List<Goal>> = _activeGoals.asStateFlow()

    private val _todaysTasks = MutableStateFlow<List<Task>>(emptyList())
    val todaysTasks: StateFlow<List<Task>> = _todaysTasks.asStateFlow()

    private val _todaysSessions = MutableStateFlow<List<FocusSession>>(emptyList())
    val todaysSessions: StateFlow<List<FocusSession>> = _todaysSessions.asStateFlow()

    private val _globalProgress = MutableStateFlow<GlobalProgressSnapshot?>(null)
    val globalProgress: StateFlow<GlobalProgressSnapshot?> = _globalProgress.asStateFlow()

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
            taskRepository.getTodaysTasks().collect {
                _todaysTasks.value = it
            }
        }

        viewModelScope.launch {
            focusSessionRepository.getTodaysSessions().collect {
                _todaysSessions.value = it
            }
        }

        viewModelScope.launch {
            progressEngine.observeGlobalProgress().collect {
                _globalProgress.value = it
            }
        }
    }
}