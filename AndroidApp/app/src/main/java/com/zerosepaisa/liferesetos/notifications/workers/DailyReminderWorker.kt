package com.zerosepaisa.liferesetos.notifications.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zerosepaisa.liferesetos.LifeResetOSApplication
import com.zerosepaisa.liferesetos.notifications.NotificationHelper

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val engine = (applicationContext as LifeResetOSApplication).appContainer.notificationEngine
        val content = engine.getDailyReminderContent() ?: return Result.success()

        NotificationHelper.showDailyReminder(
            context = applicationContext,
            missionTitle = content.missionTitle,
            taskCount = content.todaysTaskCount
        )

        return Result.success()
    }
}