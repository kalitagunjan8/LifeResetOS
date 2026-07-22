package com.zerosepaisa.liferesetos.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.zerosepaisa.liferesetos.LifeResetOSApplication
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles the non-app-opening action buttons on Task stage notifications
 * (ADR-015): Snooze and Skip. "Start Focus"/"Start Anyway"/"Reschedule"
 * are plain activity PendingIntents built in NotificationHelper and don't
 * route through this receiver.
 */
class TaskActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            NotificationManagerCompat.from(context).cancel(notificationId)
        }

        val appContext = context.applicationContext as LifeResetOSApplication
        val taskRepository = appContext.appContainer.taskRepository
        val scheduler = TaskNotificationScheduler(context.applicationContext)

        when (intent.action) {
            ACTION_SNOOZE -> {
                scheduler.snoozeStart(taskId)
            }

            ACTION_SKIP -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val task = taskRepository.getTaskById(taskId) ?: return@launch
                    taskRepository.updateTask(task.copy(status = TaskStatus.SKIPPED))
                    scheduler.cancelForTask(taskId)
                }
            }
        }
    }

    companion object {
        const val ACTION_SNOOZE = "com.zerosepaisa.liferesetos.action.TASK_SNOOZE"
        const val ACTION_SKIP = "com.zerosepaisa.liferesetos.action.TASK_SKIP"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}