package com.zerosepaisa.liferesetos.notifications.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zerosepaisa.liferesetos.LifeResetOSApplication
import com.zerosepaisa.liferesetos.notifications.NotificationHelper

class HabitReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getLong(KEY_HABIT_ID, -1L)
        if (habitId == -1L) return Result.failure()

        val habitRepository = (applicationContext as LifeResetOSApplication)
            .appContainer.habitRepository

        val habit = habitRepository.getHabitById(habitId) ?: return Result.success()
        if (!habit.reminderEnabled) return Result.success()

        NotificationHelper.showHabitReminder(
            context = applicationContext,
            habitId = habit.id,
            habitTitle = habit.title
        )

        return Result.success()
    }

    companion object {
        const val KEY_HABIT_ID = "habit_id"
    }
}