package com.zerosepaisa.liferesetos.smartplanning

import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Domain Service generating Smart Planning recommendations (ADR-015).
 *
 * Reads only Scheduled Date, Start Time, End Time, Estimated Duration and
 * Task Status. NEVER mutates user data — this engine only produces
 * recommendations; callers must require explicit user confirmation before
 * acting on any of them.
 *
 * Follows the ADR-013 Domain Service pattern: constructed from Repository
 * instances only, no DAO access.
 */
class SmartPlanningEngine(
    private val taskRepository: TaskRepository
) {

    fun observeTodaysRecommendations(): Flow<List<TaskRecommendation>> =
        taskRepository.getTodaysTasks().map { tasks -> buildRecommendations(tasks) }

    /**
     * Pure calculation — no I/O, no side effects.
     */
    fun buildRecommendations(
        tasks: List<Task>,
        nowMillis: Long = System.currentTimeMillis()
    ): List<TaskRecommendation> {
        val recommendations = mutableListOf<TaskRecommendation>()

        val plannedScheduled = tasks.filter {
            it.status == TaskStatus.PLANNED && it.scheduledDate != null && it.startTimeMinutes != null
        }

        plannedScheduled.forEach { task ->
            val scheduledDate = task.scheduledDate!!
            val startMillis = scheduledDate + task.startTimeMinutes!! * 60_000L
            val endMillis = task.endTimeMinutes?.let { scheduledDate + it * 60_000L }

            when {
                endMillis != null && nowMillis > endMillis -> {
                    recommendations.add(
                        TaskRecommendation(
                            taskId = task.id,
                            taskTitle = task.title,
                            type = RecommendationType.RESCHEDULE_MISSED_WINDOW,
                            reason = "The scheduled window for this Task has passed."
                        )
                    )
                }
                nowMillis >= startMillis -> {
                    recommendations.add(
                        TaskRecommendation(
                            taskId = task.id,
                            taskTitle = task.title,
                            type = RecommendationType.READY_TO_START,
                            reason = "This Task's start time has arrived."
                        )
                    )
                }
                else -> Unit
            }
        }

        recommendations.addAll(findScheduleConflicts(tasks))

        return recommendations
    }

    /**
     * Flags Tasks scheduled today whose start/end time windows overlap.
     * Only considers Tasks with both a start and end time set.
     */
    private fun findScheduleConflicts(tasks: List<Task>): List<TaskRecommendation> {
        val timedTasks = tasks.filter {
            it.scheduledDate != null && it.startTimeMinutes != null && it.endTimeMinutes != null
        }

        val conflicts = mutableListOf<TaskRecommendation>()
        val flaggedIds = mutableSetOf<Long>()

        for (i in timedTasks.indices) {
            for (j in i + 1 until timedTasks.size) {
                val a = timedTasks[i]
                val b = timedTasks[j]

                val overlaps = a.startTimeMinutes!! < b.endTimeMinutes!! &&
                        b.startTimeMinutes!! < a.endTimeMinutes!!

                if (overlaps) {
                    if (flaggedIds.add(a.id)) {
                        conflicts.add(
                            TaskRecommendation(
                                taskId = a.id,
                                taskTitle = a.title,
                                type = RecommendationType.SCHEDULE_CONFLICT,
                                reason = "Overlaps with \"${b.title}\"."
                            )
                        )
                    }
                    if (flaggedIds.add(b.id)) {
                        conflicts.add(
                            TaskRecommendation(
                                taskId = b.id,
                                taskTitle = b.title,
                                type = RecommendationType.SCHEDULE_CONFLICT,
                                reason = "Overlaps with \"${a.title}\"."
                            )
                        )
                    }
                }
            }
        }

        return conflicts
    }
}