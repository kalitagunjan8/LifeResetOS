package com.zerosepaisa.liferesetos.feature.goals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalCategory
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalPriority
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalsViewModel(
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

    init {
        viewModelScope.launch {
            missionRepository.getActiveMission().collect {
                _activeMission.value = it
            }
        }
    }

    /**
     * Creates a new Goal under the currently active Mission.
     * Does nothing if there is no active Mission (a Goal cannot exist without one).
     */
    fun createGoal(
        title: String,
        description: String,
        why: String,
        category: GoalCategory,
        priority: GoalPriority,
        onSaved: () -> Unit
    ) {
        val missionId = _activeMission.value?.id ?: return

        viewModelScope.launch {
            goalRepository.saveGoal(
                Goal(
                    missionId = missionId,
                    title = title,
                    description = description,
                    why = why,
                    category = category,
                    priority = priority
                )
            )
            onSaved()
        }
    }

    /**
     * Loads an existing Goal by id, for pre-filling the Edit form.
     */
    suspend fun loadGoal(goalId: Long): Goal? =
        goalRepository.getGoalById(goalId)

    /**
     * Updates an existing Goal. Preserves fields not editable on this form
     * (status, targetDate, createdAt, id, missionId) by copying from the
     * originally loaded Goal.
     */
    fun updateGoal(
        original: Goal,
        title: String,
        description: String,
        why: String,
        category: GoalCategory,
        priority: GoalPriority,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            goalRepository.updateGoal(
                original.copy(
                    title = title,
                    description = description,
                    why = why,
                    category = category,
                    priority = priority
                )
            )
            onSaved()
        }
    }

    /**
     * Deletes an existing Goal. Caller is responsible for confirming with the
     * user before invoking this.
     */
    fun deleteGoal(
        goal: Goal,
        onDeleted: () -> Unit
    ) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goal)
            onDeleted()
        }
    }
}
