package com.zerosepaisa.liferesetos.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.notifications.workers.TaskStageNotificationWorker
import java.util.concurrent.TimeUnit

/**
 * Schedules the three ADR-015 execution-window notifications (PRE_START,
 * START, END_WINDOW) for a single Task, and cancels them once the Task
 * leaves Planned. One-time WorkRequests — each stage fires at most once
 * per scheduled Task, unlike the periodic daily/habit reminders.
 */
class TaskNotificationScheduler(private val context: Context) {

    enum class Stage { PRE_START, START, END_WINDOW }

    companion object {
        private const val PRE_START_LEAD_MINUTES = 15
        private const val END_WINDOW_LEAD_MINUTES = 15
    }

    /**
     * (Re)schedules all applicable stages for this Task based on its current
     * scheduledDate/startTimeMinutes/endTimeMinutes. Safe to call on every
     * create/update — replaces any previously scheduled work for this Task.
     * No-ops (after cancelling) if the Task has no scheduledDate/startTime.
     */
    fun scheduleForTask(task: Task) {
        cancelForTask(task.id)

        val scheduledDate = task.scheduledDate ?: return
        val startMinutes = task.startTimeMinutes ?: return

        val startMillis = scheduledDate + startMinutes * 60_000L
        val now = System.currentTimeMillis()

        val preStartMillis = startMillis - PRE_START_LEAD_MINUTES * 60_000L
        if (preStartMillis > now) {
            enqueue(task.id, Stage.PRE_START, preStartMillis - now)
        }

        if (startMillis > now) {
            enqueue(task.id, Stage.START, startMillis - now)
        }

        val endMinutes = task.endTimeMinutes
        if (endMinutes != null) {
            val endMillis = scheduledDate + endMinutes * 60_000L
            val endWindowMillis = endMillis - END_WINDOW_LEAD_MINUTES * 60_000L
            if (endWindowMillis > now) {
                enqueue(task.id, Stage.END_WINDOW, endWindowMillis - now)
            }
        }
    }

    /**
     * Cancels all scheduled stage notifications for this Task. Called
     * whenever the Task leaves Planned (In Progress, Completed, Skipped,
     * Rescheduled) or is deleted, per ADR-015.
     */
    fun cancelForTask(taskId: Long) {
        val workManager = WorkManager.getInstance(context)
        Stage.entries.forEach { stage ->
            workManager.cancelUniqueWork(workNameFor(taskId, stage))
        }
    }

    /**
     * Re-enqueues just the START stage after a "Snooze 15 minutes" action.
     */
    fun snoozeStart(taskId: Long, minutes: Int = 15) {
        enqueue(taskId, Stage.START, TimeUnit.MINUTES.toMillis(minutes.toLong()))
    }

    private fun enqueue(taskId: Long, stage: Stage, delayMillis: Long) {
        val inputData = Data.Builder()
            .putLong(TaskStageNotificationWorker.KEY_TASK_ID, taskId)
            .putString(TaskStageNotificationWorker.KEY_STAGE, stage.name)
            .build()

        val request = OneTimeWorkRequestBuilder<TaskStageNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workNameFor(taskId, stage),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun workNameFor(taskId: Long, stage: Stage) = "task_${stage.name.lowercase()}_$taskId"
}