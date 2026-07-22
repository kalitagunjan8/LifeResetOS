package com.zerosepaisa.liferesetos.notifications.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zerosepaisa.liferesetos.LifeResetOSApplication
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import com.zerosepaisa.liferesetos.notifications.NotificationHelper
import com.zerosepaisa.liferesetos.notifications.TaskNotificationScheduler

class TaskStageNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        if (taskId == -1L) return Result.failure()

        val stage = inputData.getString(KEY_STAGE)
            ?.let { runCatching { TaskNotificationScheduler.Stage.valueOf(it) }.getOrNull() }
            ?: return Result.failure()

        val taskRepository = (applicationContext as LifeResetOSApplication)
            .appContainer.taskRepository

        val task = taskRepository.getTaskById(taskId) ?: return Result.success()

        // Defensive: the Task may have left Planned between scheduling and
        // firing — these notifications must never fire once a Task is
        // In Progress, Completed, Skipped or Rescheduled (ADR-015).
        if (task.status != TaskStatus.PLANNED) return Result.success()

        when (stage) {
            TaskNotificationScheduler.Stage.PRE_START ->
                NotificationHelper.showTaskPreStart(applicationContext, task.id, task.title)

            TaskNotificationScheduler.Stage.START ->
                NotificationHelper.showTaskStart(applicationContext, task.id, task.title)

            TaskNotificationScheduler.Stage.END_WINDOW -> {
                val endMinutes = task.endTimeMinutes
                val remaining = if (endMinutes != null && task.scheduledDate != null) {
                    val endMillis = task.scheduledDate + endMinutes * 60_000L
                    ((endMillis - System.currentTimeMillis()) / 60_000L).toInt().coerceAtLeast(0)
                } else {
                    0
                }
                NotificationHelper.showTaskEndWindow(applicationContext, task.id, task.title, remaining)
            }
        }

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_STAGE = "stage"
    }
}