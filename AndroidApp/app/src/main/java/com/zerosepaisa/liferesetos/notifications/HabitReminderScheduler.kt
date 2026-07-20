package com.zerosepaisa.liferesetos.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zerosepaisa.liferesetos.notifications.workers.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules/cancels a single daily reminder per Habit via WorkManager,
 * reusing the same periodic-work + delay-until-next-time pattern as
 * NotificationScheduler (v0.7.0), scoped per Habit id rather than global.
 */
class HabitReminderScheduler(private val context: Context) {

    fun scheduleReminder(habitId: Long, hour: Int, minute: Int) {
        val inputData = Data.Builder()
            .putLong(HabitReminderWorker.KEY_HABIT_ID, habitId)
            .build()

        val request = PeriodicWorkRequestBuilder<HabitReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayUntilNext(hour, minute), TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workNameFor(habitId),
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelReminder(habitId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(workNameFor(habitId))
    }

    private fun workNameFor(habitId: Long) = "habit_reminder_$habitId"

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
}