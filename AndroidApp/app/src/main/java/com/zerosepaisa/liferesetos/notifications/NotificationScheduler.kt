package com.zerosepaisa.liferesetos.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zerosepaisa.liferesetos.notifications.workers.DailyReminderWorker
import com.zerosepaisa.liferesetos.notifications.workers.EveningCheckWorker
import com.zerosepaisa.liferesetos.notifications.workers.WeeklyReviewWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleAll() {
        scheduleDailyReminder()
        scheduleEveningCheck()
        scheduleWeeklyReview()
    }

    private fun scheduleDailyReminder() {
        val delay = delayUntilNext(
            hour = NotificationDefaults.DAILY_REMINDER_HOUR,
            minute = NotificationDefaults.DAILY_REMINDER_MINUTE
        )
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleEveningCheck() {
        val delay = delayUntilNext(
            hour = NotificationDefaults.EVENING_CHECK_HOUR,
            minute = NotificationDefaults.EVENING_CHECK_MINUTE
        )
        val request = PeriodicWorkRequestBuilder<EveningCheckWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "evening_check",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleWeeklyReview() {
        val delay = delayUntilNextWeekday(
            targetDayOfWeek = NotificationDefaults.WEEKLY_REVIEW_DAY_OF_WEEK,
            hour = NotificationDefaults.WEEKLY_REVIEW_HOUR,
            minute = NotificationDefaults.WEEKLY_REVIEW_MINUTE
        )
        val request = PeriodicWorkRequestBuilder<WeeklyReviewWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weekly_review",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun delayUntilNext(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    private fun delayUntilNextWeekday(targetDayOfWeek: Int, hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            while (get(Calendar.DAY_OF_WEEK) != targetDayOfWeek || before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return target.timeInMillis - now.timeInMillis
    }
}