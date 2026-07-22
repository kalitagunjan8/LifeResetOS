package com.zerosepaisa.liferesetos.dailyreview

/**
 * Read-only facts for a single day, per ADR-015 ("Daily Review presents
 * facts only... must never shame users for missed schedules"). No scores,
 * no coaching, no derived judgment — counts only.
 */
data class DailyReviewSnapshot(
    val date: Long,
    val tasksPlanned: Int,
    val tasksStarted: Int,
    val tasksCompleted: Int,
    val tasksSkipped: Int,
    val tasksRescheduled: Int,
    val totalFocusMinutes: Int,
    val habitsCompleted: Int,
    val totalActiveHabits: Int
)