package com.zerosepaisa.liferesetos.progress

/**
 * Global (not scoped to a single Mission or Goal) progress metrics.
 * Produced by ProgressEngine.observeGlobalProgress(). See ADR-013 for
 * the exact definition of each field.
 */
data class GlobalProgressSnapshot(
    val todaysCompletionPercent: Int,
    val weeklyCompletionPercent: Int,
    val monthlyCompletionPercent: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val focusMinutesToday: Int,
    val focusMinutesThisWeek: Int,
    val totalCompletedTasks: Int,
    val totalFocusSessions: Int,
    val averageFocusScore: Int
)

/**
 * Progress for a single Mission: completed vs total Tasks across every
 * Goal that belongs to it.
 */
data class MissionProgress(
    val missionId: Long,
    val completedTasks: Int,
    val totalTasks: Int,
    val percent: Int
)

/**
 * Progress for a single Goal: completed vs total Tasks under it.
 */
data class GoalProgress(
    val goalId: Long,
    val completedTasks: Int,
    val totalTasks: Int,
    val percent: Int
)
