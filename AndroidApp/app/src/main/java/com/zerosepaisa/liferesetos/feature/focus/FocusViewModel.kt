package com.zerosepaisa.liferesetos.feature.focus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.local.entity.enums.SessionStatus
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import com.zerosepaisa.liferesetos.notifications.TaskNotificationScheduler

enum class FocusStage {
    SELECT_TASK,
    SELECT_DURATION,
    RUNNING,
    RESULT
}

class FocusViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    private val focusSessionRepository = FocusSessionRepository(
        AppDatabase.getInstance(application).focusSessionDao()
    )
    private val taskNotificationScheduler = TaskNotificationScheduler(application)

    private val _todaysTasks = MutableStateFlow<List<Task>>(emptyList())
    val todaysTasks: StateFlow<List<Task>> = _todaysTasks.asStateFlow()

    private val _stage = MutableStateFlow(FocusStage.SELECT_TASK)
    val stage: StateFlow<FocusStage> = _stage.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    private val _totalSeconds = MutableStateFlow(0)
    val totalSeconds: StateFlow<Int> = _totalSeconds.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _lastSession = MutableStateFlow<FocusSession?>(null)
    val lastSession: StateFlow<FocusSession?> = _lastSession.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            taskRepository.getTodaysTasks().collect { tasks ->
                _todaysTasks.value = tasks.filter { !it.isCompleted }
            }
        }
    }

    fun selectTask(task: Task) {
        _selectedTask.value = task
        _stage.value = FocusStage.SELECT_DURATION
    }

    /**
     * Starts a Focus Session for the selected Task. Used for both preset
     * and custom durations — same logic either way, per ADR-012.
     */
    fun startSession(minutes: Int) {
        if (minutes <= 0) return

        val totalSeconds = minutes * 60
        _totalSeconds.value = totalSeconds
        _remainingSeconds.value = totalSeconds
        _stage.value = FocusStage.RUNNING

        _selectedTask.value?.let { task ->
            viewModelScope.launch {
                taskRepository.updateTask(task.copy(status = TaskStatus.IN_PROGRESS))
                taskNotificationScheduler.cancelForTask(task.id)
            }
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            finishSession(SessionStatus.COMPLETED)
        }
    }

    /**
     * User manually stops the session before the timer reaches zero.
     */
    fun endEarly() {
        timerJob?.cancel()
        finishSession(SessionStatus.ENDED_EARLY)
    }

    private fun finishSession(status: SessionStatus) {
        val task = _selectedTask.value ?: return
        val total = _totalSeconds.value
        val actual = total - _remainingSeconds.value

        val focusScore = if (total > 0) {
            ((actual.toFloat() / total.toFloat()) * 100f).toInt().coerceIn(0, 100)
        } else {
            0
        }

        val now = System.currentTimeMillis()

        val session = FocusSession(
            taskId = task.id,
            plannedDurationSeconds = total,
            actualDurationSeconds = actual,
            status = status,
            focusScore = focusScore,
            startedAt = now - (actual * 1000L),
            endedAt = now
        )

        viewModelScope.launch {
            focusSessionRepository.saveSession(session)
        }

        _lastSession.value = session
        _stage.value = FocusStage.RESULT
    }

    /**
     * Returns to Task selection after viewing a Result, ready to start
     * another session.
     */
    fun startOver() {
        timerJob?.cancel()
        _selectedTask.value = null
        _lastSession.value = null
        _totalSeconds.value = 0
        _remainingSeconds.value = 0
        _stage.value = FocusStage.SELECT_TASK
    }


    fun completeTask() {
        _selectedTask.value?.let { task ->
            viewModelScope.launch {
                taskRepository.updateTask(
                    task.copy(
                        isCompleted = true,
                        completedAt = System.currentTimeMillis(),
                        status = TaskStatus.COMPLETED
                    )
                )
                taskNotificationScheduler.cancelForTask(task.id)
            }
        }
        startOver()
    }


    fun continueLater() {
        startOver()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
