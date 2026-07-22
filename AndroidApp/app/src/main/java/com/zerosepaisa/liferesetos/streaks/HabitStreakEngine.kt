package com.zerosepaisa.liferesetos.streaks

import com.zerosepaisa.liferesetos.data.repository.HabitCompletionRepository
import com.zerosepaisa.liferesetos.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class HabitStreak(
    val currentStreak: Int,
    val longestStreak: Int
)


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