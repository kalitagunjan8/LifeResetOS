package com.zerosepaisa.liferesetos.smartplanning

/**
 * A recommendation is a suggestion only. Per ADR-015, Smart Planning must
 * never modify user data automatically — every recommendation requires
 * explicit user confirmation from the caller before any Task is mutated.
 */
enum class RecommendationType {
    READY_TO_START,
    RESCHEDULE_MISSED_WINDOW,
    SCHEDULE_CONFLICT
}

data class TaskRecommendation(
    val taskId: Long,
    val taskTitle: String,
    val type: RecommendationType,
    val reason: String
)