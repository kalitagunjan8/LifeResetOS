package com.zerosepaisa.liferesetos.feature.progress

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.progress.GlobalProgressSnapshot
import com.zerosepaisa.liferesetos.progress.GoalProgress
import com.zerosepaisa.liferesetos.progress.MissionProgress
import com.zerosepaisa.liferesetos.progress.ProgressEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Display-only pairing of a Goal's title with its ProgressEngine-computed
 * GoalProgress. Not a calculation — every number here (completedTasks,
 * totalTasks, percent) already comes straight from GoalProgress; this just
 * carries the title alongside it for the list UI. No arithmetic happens
 * here (per ADR-013: ProgressEngine remains the only source of derived
 * metrics).
 */
data class GoalProgressItem(
    val goalId: Long,
    val title: String,
    val progress: GoalProgress
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModel(
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
     * Built from the same Repository instances above, per ADR-013 — this
     * ViewModel coordinates both the Repositories and the domain service.
     */
    private val progressEngine = ProgressEngine(
        missionRepository = missionRepository,
        goalRepository = goalRepository,
        taskRepository = taskRepository,
        focusSessionRepository = focusSessionRepository
    )

    private val _globalProgress = MutableStateFlow<GlobalProgressSnapshot?>(null)
    val globalProgress: StateFlow<GlobalProgressSnapshot?> = _globalProgress.asStateFlow()

    private val _missionProgress = MutableStateFlow<MissionProgress?>(null)
    val missionProgress: StateFlow<MissionProgress?> = _missionProgress.asStateFlow()

    private val _goalProgressItems = MutableStateFlow<List<GoalProgressItem>>(emptyList())
    val goalProgressItems: StateFlow<List<GoalProgressItem>> = _goalProgressItems.asStateFlow()

    init {
        viewModelScope.launch {
            progressEngine.observeGlobalProgress().collect {
                _globalProgress.value = it
            }
        }

        // Mission Progress follows whichever Mission is currently active.
        viewModelScope.launch {
            missionRepository.getActiveMission()
                .flatMapLatest { mission ->
                    if (mission != null) {
                        progressEngine.observeMissionProgress(mission.id)
                    } else {
                        flowOf(null)
                    }
                }
                .collect {
                    _missionProgress.value = it
                }
        }

        // Goal Progress list follows the current set of active Goals,
        // re-subscribing to each Goal's progress whenever that set changes.
        viewModelScope.launch {
            goalRepository.getActiveGoals()
                .flatMapLatest { goals ->
                    if (goals.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        val perGoalFlows = goals.map { goal ->
                            progressEngine.observeGoalProgress(goal.id).map { progress ->
                                GoalProgressItem(
                                    goalId = goal.id,
                                    title = goal.title,
                                    progress = progress
                                )
                            }
                        }
                        combine(perGoalFlows) { items -> items.toList() }
                    }
                }
                .collect {
                    _goalProgressItems.value = it
                }
        }
    }
}
