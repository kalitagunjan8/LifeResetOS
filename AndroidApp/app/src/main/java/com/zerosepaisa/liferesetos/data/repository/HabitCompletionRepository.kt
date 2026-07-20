package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.HabitCompletionDao
import com.zerosepaisa.liferesetos.data.local.entity.HabitCompletion
import com.zerosepaisa.liferesetos.util.DateUtils

class HabitCompletionRepository(
    private val habitCompletionDao: HabitCompletionDao
) {

    /**
     * Completions for the current calendar day, across all Habits. Used by
     * Journey to show today's completion state per Habit.
     */
    fun getTodaysCompletions() =
        habitCompletionDao.getCompletionsForDate(DateUtils.startOfToday())

    fun getCompletionsForHabit(habitId: Long) =
        habitCompletionDao.getCompletionsForHabit(habitId)

    /**
     * Toggles today's completion for the given Habit: inserts a completion
     * row if none exists for today, removes it if one already does. This
     * is what enforces "once per day" and lets a new calendar day become
     * available automatically (no row exists for the new date yet).
     */
    suspend fun toggleTodaysCompletion(habitId: Long) {
        val today = DateUtils.startOfToday()
        val existing = habitCompletionDao.getCompletionForHabitAndDate(habitId, today)
        if (existing != null) {
            habitCompletionDao.delete(existing)
        } else {
            habitCompletionDao.insert(
                HabitCompletion(habitId = habitId, completedDate = today)
            )
        }
    }
}