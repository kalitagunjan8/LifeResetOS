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
import com.zerosepaisa.liferesetos.notifications.HabitReminderScheduler
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import com.zerosepaisa.liferesetos.notifications.TaskNotificationScheduler
import com.zerosepaisa.liferesetos.util.DateUtils

data class JourneyTaskItem(
    val task: Task,
    val goalId: Long,
    val goalTitle: String
)


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

    private val _tasksByGoal = MutableStateFlow<Map<Long, List<Task>>>(emptyMap())
    val tasksByGoal: StateFlow<Map<Long, List<Task>>> = _tasksByGoal.asStateFlow()

    private val _taskItems = MutableStateFlow<List<JourneyTaskItem>>(emptyList())
    val taskItems: StateFlow<List<JourneyTaskItem>> = _taskItems.asStateFlow()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _todaysCompletedHabitIds = MutableStateFlow<Set<Long>>(emptySet())
    val todaysCompletedHabitIds: StateFlow<Set<Long>> = _todaysCompletedHabitIds.asStateFlow()

    private val _habitStreaks = MutableStateFlow<Map<Long, HabitStreak>>(emptyMap())
    val habitStreaks: StateFlow<Map<Long, HabitStreak>> = _habitStreaks.asStateFlow()

    private val habitReminderScheduler = HabitReminderScheduler(application)

    private val taskNotificationScheduler = TaskNotificationScheduler(application)

    private val taskRepository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

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

        viewModelScope.launch {
            goalRepository.getActiveGoals()
                .flatMapLatest { goals ->
                    if (goals.isEmpty()) {
                        flowOf(goals to emptyMap<Long, List<Task>>())
                    } else {
                        val perGoalFlows = goals.map { goal ->
                            taskRepository.getTasksForGoal(goal.id).map { tasks -> goal.id to tasks }
                        }
                        combine(perGoalFlows) { pairs -> goals to pairs.toMap() }
                    }
                }
                .collect { (goals, map) ->
                    _tasksByGoal.value = map
                    _taskItems.value = sortJourneyTaskItems(
                        goals.flatMap { goal ->
                            map[goal.id].orEmpty().map { task ->
                                JourneyTaskItem(task = task, goalId = goal.id, goalTitle = goal.title)
                            }
                        }
                    )
                }
        }
    }

    fun createHabit(
        title: String,
        description: String,
        reminderEnabled: Boolean = false,
        reminderHour: Int? = null,
        reminderMinute: Int? = null
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val habitId = habitRepository.saveHabit(
                Habit(
                    title = title,
                    description = description,
                    reminderEnabled = reminderEnabled,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute
                )
            )

            if (reminderEnabled && reminderHour != null && reminderMinute != null) {
                habitReminderScheduler.scheduleReminder(habitId, reminderHour, reminderMinute)
            }
        }
    }

    fun updateHabit(
        original: Habit,
        title: String,
        description: String,
        isActive: Boolean,
        reminderEnabled: Boolean = false,
        reminderHour: Int? = null,
        reminderMinute: Int? = null
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            habitRepository.updateHabit(
                original.copy(
                    title = title,
                    description = description,
                    isActive = isActive,
                    reminderEnabled = reminderEnabled,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute
                )
            )

            if (reminderEnabled && reminderHour != null && reminderMinute != null) {
                habitReminderScheduler.scheduleReminder(original.id, reminderHour, reminderMinute)
            } else {
                habitReminderScheduler.cancelReminder(original.id)
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
            habitReminderScheduler.cancelReminder(habit.id)
        }
    }

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            habitCompletionRepository.toggleTodaysCompletion(habit.id)
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
            val taskId = taskRepository.saveTask(
                Task(
                    goalId = goalId,
                    title = title,
                    scheduledDate = scheduledDate,
                    startTimeMinutes = startTimeMinutes,
                    endTimeMinutes = endTimeMinutes,
                    estimatedDurationMinutes = estimatedDurationMinutes
                )
            )
            taskRepository.getTaskById(taskId)?.let {taskNotificationScheduler.scheduleForTask(it)}
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
            val updated = task.copy(
                title = newTitle,
                scheduledDate = scheduledDate,
                startTimeMinutes = startTimeMinutes,
                endTimeMinutes = endTimeMinutes,
                estimatedDurationMinutes = estimatedDurationMinutes
            )
            taskRepository.updateTask(updated)
            if (updated.status == TaskStatus.PLANNED) {
                taskNotificationScheduler.scheduleForTask(updated)
            } else {
                taskNotificationScheduler.cancelForTask(updated.id)
            }
        }
    }
    /**
     * Reschedules a Task via the lightweight Journey reschedule picker
     * (date only). Resets status to PLANNED so the Task re-enters the
     * normal Planned lifecycle, mirroring updateTask()'s existing pattern.
     */
    fun rescheduleTask(task: Task, newScheduledDate: Long) {
        viewModelScope.launch {
            val updated = task.copy(
                scheduledDate = newScheduledDate,
                status = TaskStatus.PLANNED
            )
            taskRepository.updateTask(updated)
            taskNotificationScheduler.scheduleForTask(updated)
        }
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null,
                status = if (!task.isCompleted) TaskStatus.COMPLETED else TaskStatus.PLANNED
            )
            taskRepository.updateTask(updated)
            if (updated.status == TaskStatus.PLANNED) {
                taskNotificationScheduler.scheduleForTask(updated)
            } else {
                taskNotificationScheduler.cancelForTask(updated.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            taskNotificationScheduler.cancelForTask(task.id)
        }
    }
    /**
     * Orders Journey's flat Task list: Today's Tasks first, then Future
     * (soonest first), then Past (most recent first), then Unscheduled last.
     * Scoped to Journey only — Goal Detail and Focus/Today's Actions are
     * unaffected and keep their own existing ordering.
     */
    private fun sortJourneyTaskItems(items: List<JourneyTaskItem>): List<JourneyTaskItem> {
        val today = DateUtils.startOfToday()
        return items.sortedWith(
            compareBy<JourneyTaskItem> { item ->
                val date = item.task.scheduledDate
                when {
                    date == null -> 3
                    date == today -> 0
                    date > today -> 1
                    else -> 2
                }
            }.thenBy { item ->
                val date = item.task.scheduledDate
                when {
                    date == null -> 0L
                    date > today -> date
                    else -> -date
                }
            }
        )
    }
}