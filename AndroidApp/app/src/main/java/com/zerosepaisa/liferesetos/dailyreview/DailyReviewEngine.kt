package com.zerosepaisa.liferesetos.dailyreview

import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.HabitCompletionRepository
import com.zerosepaisa.liferesetos.data.repository.HabitRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Domain Service producing today's Daily Review facts (ADR-015).
 *
 * Follows the ADR-013 Domain Service pattern: constructed from Repository
 * instances only, no DAO access, pure aggregation, no UI concerns.
 */
class DailyReviewEngine(
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository
) {

    fun observeTodaysReview(): Flow<DailyReviewSnapshot> =
        combine(
            taskRepository.getTodaysTasks(),
            focusSessionRepository.getTodaysSessions(),
            habitRepository.getAllHabits(),
            habitCompletionRepository.getTodaysCompletions()
        ) { todaysTasks, todaysSessions, habits, todaysCompletions ->
            val completedHabitIds = todaysCompletions.map { it.habitId }.toSet()

            DailyReviewSnapshot(
                date = DateUtils.startOfToday(),
                tasksPlanned = todaysTasks.count { it.status == TaskStatus.PLANNED },
                tasksStarted = todaysTasks.count { it.status == TaskStatus.IN_PROGRESS },
                tasksCompleted = todaysTasks.count { it.status == TaskStatus.COMPLETED },
                tasksSkipped = todaysTasks.count { it.status == TaskStatus.SKIPPED },
                tasksRescheduled = todaysTasks.count { it.status == TaskStatus.RESCHEDULED },
                totalFocusMinutes = todaysSessions.sumOf { it.actualDurationSeconds } / 60,
                habitsCompleted = habits.count { it.isActive && completedHabitIds.contains(it.id) },
                totalActiveHabits = habits.count { it.isActive }
            )
        }
}