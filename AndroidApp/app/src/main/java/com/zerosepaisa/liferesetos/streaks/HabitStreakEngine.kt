package com.zerosepaisa.liferesetos.streaks

import com.zerosepaisa.liferesetos.data.repository.HabitCompletionRepository
import com.zerosepaisa.liferesetos.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Derived streak metrics for a single Habit. Never persisted — always
 * calculated from HabitCompletion history (per v0.8.3 scope).
 */
data class HabitStreak(
    val currentStreak: Int,
    val longestStreak: Int
)

/**
 * Domain Service for Habit streak calculations, following the same
 * pattern as ProgressEngine and NotificationEngine (ADR-013): a plain
 * Kotlin class depending only on a Repository, performing pure derived
 * calculations, exposing reactive Flows. Coordinated by the ViewModel
 * alongside the Repository layer — not a replacement for it.
 *
 * A "streak day" here is any calendar day with at least one
 * HabitCompletion row for that Habit (HabitCompletion.completedDate is
 * already a start-of-day epoch millis value, and the unique index on
 * (habitId, completedDate) guarantees multiple completions on the same
 * day never produce duplicate days — see ADR pattern used by
 * ProgressEngine's identical Task-streak logic).
 */
class HabitStreakEngine(
    private val habitCompletionRepository: HabitCompletionRepository
) {

    fun observeStreakForHabit(habitId: Long): Flow<HabitStreak> =
        habitCompletionRepository.getCompletionsForHabit(habitId).map { completions ->
            val days = completions.map { it.completedDate }.toSet()
            HabitStreak(
                currentStreak = calculateCurrentStreak(days),
                longestStreak = calculateLongestStreak(days)
            )
        }

    /**
     * Counts backward from today, with a grace period: if today has no
     * completion yet, today is skipped (today isn't over) rather than
     * breaking the streak, and counting starts from yesterday instead.
     */
    private fun calculateCurrentStreak(days: Set<Long>): Int {
        if (days.isEmpty()) return 0

        var cursor = DateUtils.startOfToday()

        if (!days.contains(cursor)) {
            cursor -= ONE_DAY_MILLIS
            if (!days.contains(cursor)) return 0
        }

        var streak = 0
        while (days.contains(cursor)) {
            streak++
            cursor -= ONE_DAY_MILLIS
        }
        return streak
    }

    /**
     * Longest run of consecutive streak-days across all history.
     */
    private fun calculateLongestStreak(days: Set<Long>): Int {
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