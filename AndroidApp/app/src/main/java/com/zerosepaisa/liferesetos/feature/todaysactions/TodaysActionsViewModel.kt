package com.zerosepaisa.liferesetos.feature.todaysactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodaysActionsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    private val _todaysTasks = MutableStateFlow<List<Task>>(emptyList())
    val todaysTasks: StateFlow<List<Task>> = _todaysTasks.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository.getTodaysTasks().collect {
                _todaysTasks.value = it
            }
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
}
