package com.zerosepaisa.liferesetos.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val DAILY_REMINDER = "channel_daily_reminder"
    const val TODAYS_ACTIONS = "channel_todays_actions"
    const val FOCUS_REMINDER = "channel_focus_reminder"
    const val WEEKLY_REVIEW = "channel_weekly_review"

    const val HABIT_REMINDER = "channel_habit_reminder"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                DAILY_REMINDER,
                "Daily Mission Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminds you of today's focus on your Mission" }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                TODAYS_ACTIONS,
                "Today's Actions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminds you about incomplete Tasks scheduled today" }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                FOCUS_REMINDER,
                "Focus Session Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Nudges you to start a Focus Session" }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                WEEKLY_REVIEW,
                "Weekly Review",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Weekly summary of your Progress" }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                HABIT_REMINDER,
                "Habit Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminds you to complete a scheduled Habit" }
        )
    }
}