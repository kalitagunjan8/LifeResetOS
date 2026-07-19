// notifications/workers/EveningCheckWorker.kt (UPDATED)

package com.zerosepaisa.liferesetos.notifications.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zerosepaisa.liferesetos.LifeResetOSApplication
import com.zerosepaisa.liferesetos.notifications.NotificationHelper

class EveningCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val engine = (applicationContext as LifeResetOSApplication).appContainer.notificationEngine

        engine.getTodaysActionsReminderContent()?.let { content ->
            NotificationHelper.showTodaysActionsReminder(
                context = applicationContext,
                incompleteCount = content.incompleteCount,
                totalCount = content.totalCount
            )
        }

        engine.getFocusReminderContent()?.let { content ->
            NotificationHelper.showFocusReminder(
                context = applicationContext,
                incompleteCount = content.incompleteScheduledTaskCount
            )
        }

        return Result.success()
    }
}