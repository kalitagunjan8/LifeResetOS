package com.zerosepaisa.liferesetos.feature.goaldetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val goalRepository = GoalRepository(
        AppDatabase.getInstance(application).goalDao()
    )

    private val taskRepository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private var tasksJob: Job? = null

    /**
     * Loads the Goal once. Call again (e.g. on resume) to pick up edits
     * made on the Goals Edit screen.
     */
    suspend fun loadGoal(goalId: Long): Goal? =
        goalRepository.getGoalById(goalId)

    /**
     * Starts (or restarts) observing this Goal's Tasks. Safe to call
     * multiple times; cancels any prior collection first.
     */
    fun observeTasks(goalId: Long) {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.getTasksForGoal(goalId).collect {
                _tasks.value = it
            }
        }
    }

    fun addTask(
        goalId: Long,
        title: String,
        scheduledDate: Long?,
        startTimeMinutes: Int? = null,
        endTimeMinutes: Int? = null,
        estimatedDurationMinutes: Int? = null
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            taskRepository.saveTask(
                Task(
                    goalId = goalId,
                    title = title,
                    scheduledDate = scheduledDate,
                    startTimeMinutes = startTimeMinutes,
                    endTimeMinutes = endTimeMinutes,
                    estimatedDurationMinutes = estimatedDurationMinutes
                )
            )
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(
                    isCompleted = !task.isCompleted,
                    completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
                )
            )
        }
    }

    fun updateTask(
        task: Task,
        newTitle: String,
        scheduledDate: Long?,
        startTimeMinutes: Int? = null,
        endTimeMinutes: Int? = null,
        estimatedDurationMinutes: Int? = null
    ) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(
                    title = newTitle,
                    scheduledDate = scheduledDate,
                    startTimeMinutes = startTimeMinutes,
                    endTimeMinutes = endTimeMinutes,
                    estimatedDurationMinutes = estimatedDurationMinutes
                )
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}
