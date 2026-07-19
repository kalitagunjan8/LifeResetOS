// notifications/workers/WeeklyReviewWorker.kt (UPDATED)

package com.zerosepaisa.liferesetos.notifications.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zerosepaisa.liferesetos.LifeResetOSApplication
import com.zerosepaisa.liferesetos.notifications.NotificationHelper

class WeeklyReviewWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val engine = (applicationContext as LifeResetOSApplication).appContainer.notificationEngine
        val content = engine.getWeeklyReviewContent()

        NotificationHelper.showWeeklyReview(
            context = applicationContext,
            tasksCompleted = content.tasksCompletedThisWeek,
            focusSessions = content.focusSessionsThisWeek,
            focusMinutes = content.focusMinutesThisWeek
        )

        return Result.success()
    }
}