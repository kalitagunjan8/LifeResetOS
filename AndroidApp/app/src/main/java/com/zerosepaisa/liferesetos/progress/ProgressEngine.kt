package com.zerosepaisa.liferesetos.progress

import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

/**
 * The single source of truth for derived/calculated progress metrics
 * (percentages, streaks, aggregates). See ADR-013 for the architectural
 * decision and the concrete definition behind every metric below.
 *
 * ProgressEngine depends only on Repositories, never DAOs — it does no
 * direct Room access. It performs pure calculations over reactive data
 * the Repositories already expose, and re-emits whenever that underlying
 * data changes (Room remains the source of truth, per ADR-009).
 *
 * GoalRepository and MissionRepository are accepted for API symmetry and
 * future use (e.g. Mission title lookups alongside progress), even though
 * the current calculations only need TaskRepository/FocusSessionRepository.
 */
class ProgressEngine(
    private val missionRepository: MissionRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository
) {

    /**
     * Task-scoped intermediate bundle, combined internally before merging
     * with session data. Not part of the public API.
     */
    private data class TaskMetricsInput(
        val today: List<Task>,
        val week: List<Task>,
        val month: List<Task>,
        val all: List<Task>
    )

    /**
     * Focus Session-scoped intermediate bundle, combined internally before
     * merging with task data. Not part of the public API.
     */
    private data class SessionMetricsInput(
        val today: List<FocusSession>,
        val week: List<FocusSession>,
        val all: List<FocusSession>
    )

    /**
     * Global (not Mission/Goal-scoped) progress metrics. Recomputes
     * reactively whenever the underlying Tasks or Focus Sessions change.
     */
    fun observeGlobalProgress(): Flow<GlobalProgressSnapshot> {

        val taskMetricsFlow: Flow<TaskMetricsInput> = combine(
            taskRepository.getTodaysTasks(),
            taskRepository.getTasksScheduledBetween(DateUtils.startOfWeek(), DateUtils.endOfWeek()),
            taskRepository.getTasksScheduledBetween(DateUtils.startOfMonth(), DateUtils.endOfMonth()),
            taskRepository.getAllTasks()
        ) { today, week, month, all ->
            TaskMetricsInput(today = today, week = week, month = month, all = all)
        }

        val sessionMetricsFlow: Flow<SessionMetricsInput> = combine(
            focusSessionRepository.getTodaysSessions(),
            focusSessionRepository.getSessionsBetween(DateUtils.startOfWeek(), DateUtils.endOfWeek()),
            focusSessionRepository.getAllSessions()
        ) { today, week, all ->
            SessionMetricsInput(today = today, week = week, all = all)
        }

        return combine(taskMetricsFlow, sessionMetricsFlow) { taskMetrics, sessionMetrics ->
            GlobalProgressSnapshot(
                todaysCompletionPercent = completionPercent(taskMetrics.today),
                weeklyCompletionPercent = completionPercent(taskMetrics.week),
                monthlyCompletionPercent = completionPercent(taskMetrics.month),
                currentStreak = calculateCurrentStreak(taskMetrics.all),
                longestStreak = calculateLongestStreak(taskMetrics.all),
                focusMinutesToday = totalMinutes(sessionMetrics.today),
                focusMinutesThisWeek = totalMinutes(sessionMetrics.week),
                totalCompletedTasks = taskMetrics.all.count { it.isCompleted },
                totalFocusSessions = sessionMetrics.all.size,
                averageFocusScore = averageScore(sessionMetrics.all)
            )
        }
    }

    /**
     * Progress for a single Mission: completed vs total Tasks across every
     * Goal that belongs to it (per ADR-013's Mission Progress % definition).
     */
    fun observeMissionProgress(missionId: Long): Flow<MissionProgress> =
        taskRepository.getTasksForMission(missionId).map { tasks ->
            val completed = tasks.count { it.isCompleted }
            MissionProgress(
                missionId = missionId,
                completedTasks = completed,
                totalTasks = tasks.size,
                percent = percentOf(completed, tasks.size)
            )
        }

    /**
     * Progress for a single Goal: completed vs total Tasks under it.
     */
    fun observeGoalProgress(goalId: Long): Flow<GoalProgress> =
        taskRepository.getTasksForGoal(goalId).map { tasks ->
            val completed = tasks.count { it.isCompleted }
            GoalProgress(
                goalId = goalId,
                completedTasks = completed,
                totalTasks = tasks.size,
                percent = percentOf(completed, tasks.size)
            )
        }

    // ---- Calculation helpers (pure, no I/O) ----

    private fun completionPercent(tasks: List<Task>): Int =
        percentOf(tasks.count { it.isCompleted }, tasks.size)

    private fun percentOf(numerator: Int, denominator: Int): Int {
        if (denominator == 0) return 0
        return ((numerator.toDouble() / denominator.toDouble()) * 100)
            .roundToInt()
            .coerceIn(0, 100)
    }

    private fun totalMinutes(sessions: List<FocusSession>): Int =
        sessions.sumOf { it.actualDurationSeconds } / 60

    private fun averageScore(sessions: List<FocusSession>): Int {
        if (sessions.isEmpty()) return 0
        return sessions.sumOf { it.focusScore } / sessions.size
    }

    /**
     * A "streak day" is any calendar day with at least one completed Task
     * (per ADR-013). Returns the distinct set of streak-days as
     * start-of-day epoch millis.
     */
    private fun streakDays(tasks: List<Task>): Set<Long> =
        tasks
            .filter { it.isCompleted && it.completedAt != null }
            .map { DateUtils.startOfDay(it.completedAt!!) }
            .toSet()

    /**
     * Counts backward from today. If today has no completions yet, today
     * is skipped (grace period) rather than breaking the streak, since
     * today isn't over yet (per ADR-013).
     */
    private fun calculateCurrentStreak(tasks: List<Task>): Int {
        val days = streakDays(tasks)
        if (days.isEmpty()) return 0

        val oneDayMillis = ONE_DAY_MILLIS
        var cursor = DateUtils.startOfToday()

        if (!days.contains(cursor)) {
            // Grace period: today isn't done yet, start from yesterday.
            cursor -= oneDayMillis
            if (!days.contains(cursor)) return 0
        }

        var streak = 0
        while (days.contains(cursor)) {
            streak++
            cursor -= oneDayMillis
        }
        return streak
    }

    /**
     * Longest run of consecutive streak-days across all history.
     */
    private fun calculateLongestStreak(tasks: List<Task>): Int {
        val days = streakDays(tasks)
        if (days.isEmpty()) return 0

        val sortedDays = days.sorted()

        var longest = 1
        var current = 1

        for (i in 1 until sortedDays.size) {
            current = if (sortedDays[i] - sortedDays[i - 1] == ONE_DAY_MILLIS) {
                current + 1
            } else {
                1
            }
            if (current > longest) longest = current
        }

        return longest
    }

    companion object {
        private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
