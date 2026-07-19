// notifications/NotificationEngine.kt (UPDATED — uses DateUtils.startOfWeek()/endOfWeek() instead)

package com.zerosepaisa.liferesetos.notifications

import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.util.DateUtils
import kotlinx.coroutines.flow.first

data class DailyReminderContent(
    val missionTitle: String,
    val todaysTaskCount: Int
)

data class TodaysActionsReminderContent(
    val incompleteCount: Int,
    val totalCount: Int
)

data class FocusReminderContent(
    val incompleteScheduledTaskCount: Int
)

data class WeeklyReviewContent(
    val tasksCompletedThisWeek: Int,
    val focusSessionsThisWeek: Int,
    val focusMinutesThisWeek: Int
)

class NotificationEngine(
    private val missionRepository: MissionRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository
) {

    suspend fun getDailyReminderContent(): DailyReminderContent? {
        val mission = missionRepository.getActiveMission().first() ?: return null
        val todaysTasks = taskRepository.getTodaysTasks().first()
        return DailyReminderContent(
            missionTitle = mission.title,
            todaysTaskCount = todaysTasks.size
        )
    }

    suspend fun getTodaysActionsReminderContent(): TodaysActionsReminderContent? {
        val todaysTasks = taskRepository.getTodaysTasks().first()
        if (todaysTasks.isEmpty()) return null
        val incomplete = todaysTasks.count { !it.isCompleted }
        if (incomplete == 0) return null
        return TodaysActionsReminderContent(
            incompleteCount = incomplete,
            totalCount = todaysTasks.size
        )
    }

    suspend fun getFocusReminderContent(): FocusReminderContent? {
        val todaysTasks = taskRepository.getTodaysTasks().first()
        val incomplete = todaysTasks.count { !it.isCompleted }
        if (incomplete == 0) return null
        return FocusReminderContent(incompleteScheduledTaskCount = incomplete)
    }

    suspend fun getWeeklyReviewContent(): WeeklyReviewContent {
        val weekStart = DateUtils.startOfWeek()
        val weekEnd = DateUtils.endOfWeek()
        val tasksThisWeek = taskRepository.getTasksScheduledBetween(weekStart, weekEnd).first()
        val completedThisWeek = tasksThisWeek.count { it.isCompleted }
        val sessionsThisWeek = focusSessionRepository.getSessionsBetween(weekStart, weekEnd).first()
        val focusMinutes = sessionsThisWeek.sumOf { it.actualDurationSeconds } / 60

        return WeeklyReviewContent(
            tasksCompletedThisWeek = completedThisWeek,
            focusSessionsThisWeek = sessionsThisWeek.size,
            focusMinutesThisWeek = focusMinutes
        )
    }
}