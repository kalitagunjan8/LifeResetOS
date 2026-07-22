package com.zerosepaisa.liferesetos.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zerosepaisa.liferesetos.MainActivity
import com.zerosepaisa.liferesetos.R
import com.zerosepaisa.liferesetos.navigation.Routes

object NotificationHelper {

    const val EXTRA_DEEP_LINK_ROUTE = "deep_link_route"

    private const val HABIT_REMINDER_ID_BASE = 2000

    private const val TASK_PRE_START_ID_BASE = 3000
    private const val TASK_START_ID_BASE = 4000
    private const val TASK_END_WINDOW_ID_BASE = 5000

    private fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun buildPendingIntent(context: Context, route: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DEEP_LINK_ROUTE, route)
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildTaskActionPendingIntent(
        context: Context,
        action: String,
        taskId: Long,
        notificationId: Int,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, TaskActionReceiver::class.java).apply {
            this.action = action
            putExtra(TaskActionReceiver.EXTRA_TASK_ID, taskId)
            putExtra(TaskActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    fun showDailyReminder(context: Context, missionTitle: String, taskCount: Int) {
        if (!hasPermission(context)) return

        val text = if (taskCount > 0) {
            "$taskCount Task${if (taskCount == 1) "" else "s"} scheduled today toward \"$missionTitle\""
        } else {
            "No Tasks scheduled today toward \"$missionTitle\" yet — plan one in Today's Actions"
        }

        val notification = NotificationCompat.Builder(context, NotificationChannels.DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Today's focus: $missionTitle")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildPendingIntent(context, Routes.TODAYS_ACTIONS, 1001))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    @SuppressLint("MissingPermission")
    fun showTodaysActionsReminder(context: Context, incompleteCount: Int, totalCount: Int) {
        if (!hasPermission(context)) return

        val text = "$incompleteCount of $totalCount Tasks still open today"

        val notification = NotificationCompat.Builder(context, NotificationChannels.TODAYS_ACTIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Today's Actions")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildPendingIntent(context, Routes.TODAYS_ACTIONS, 1002))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1002, notification)
    }

    @SuppressLint("MissingPermission")
    fun showFocusReminder(context: Context, incompleteCount: Int) {
        if (!hasPermission(context)) return

        val text = "$incompleteCount Task${if (incompleteCount == 1) "" else "s"} still waiting for a Focus Session today"

        val notification = NotificationCompat.Builder(context, NotificationChannels.FOCUS_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Start a Focus Session")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildPendingIntent(context, Routes.FOCUS, 1003))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1003, notification)
    }

    @SuppressLint("MissingPermission")
    fun showWeeklyReview(context: Context, tasksCompleted: Int, focusSessions: Int, focusMinutes: Int) {
        if (!hasPermission(context)) return

        val text = "$tasksCompleted Tasks completed · $focusSessions Focus Sessions · $focusMinutes min focused"

        val notification = NotificationCompat.Builder(context, NotificationChannels.WEEKLY_REVIEW)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Your Weekly Review is ready")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildPendingIntent(context, Routes.PROGRESS, 1004))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1004, notification)
    }

    @SuppressLint("MissingPermission")
    fun showHabitReminder(context: Context, habitId: Long, habitTitle: String) {
        if (!hasPermission(context)) return

        val notificationId = HABIT_REMINDER_ID_BASE + habitId.toInt()

        val notification = NotificationCompat.Builder(context, NotificationChannels.HABIT_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Habit Reminder")
            .setContentText("Time for: $habitTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildPendingIntent(context, Routes.JOURNEY, notificationId))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    @SuppressLint("MissingPermission")
    fun showTaskPreStart(context: Context, taskId: Long, taskTitle: String) {
        if (!hasPermission(context)) return

        val notificationId = TASK_PRE_START_ID_BASE + taskId.toInt()
        val startFocusIntent = buildPendingIntent(context, Routes.FOCUS, notificationId)

        val notification = NotificationCompat.Builder(context, NotificationChannels.TASK_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(taskTitle)
            .setContentText("Starts in 15 minutes.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(startFocusIntent)
            .addAction(0, "▶ Start Focus", startFocusIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    @SuppressLint("MissingPermission")
    fun showTaskStart(context: Context, taskId: Long, taskTitle: String) {
        if (!hasPermission(context)) return

        val notificationId = TASK_START_ID_BASE + taskId.toInt()
        val startFocusIntent = buildPendingIntent(context, Routes.FOCUS, notificationId)
        val snoozeIntent = buildTaskActionPendingIntent(
            context, TaskActionReceiver.ACTION_SNOOZE, taskId, notificationId, notificationId + 1
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.TASK_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(taskTitle)
            .setContentText("It's time to begin.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(startFocusIntent)
            .addAction(0, "▶ Start Focus", startFocusIntent)
            .addAction(0, "Snooze 15 minutes", snoozeIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    @SuppressLint("MissingPermission")
    fun showTaskEndWindow(context: Context, taskId: Long, taskTitle: String, minutesRemaining: Int) {
        if (!hasPermission(context)) return

        val notificationId = TASK_END_WINDOW_ID_BASE + taskId.toInt()
        val startAnywayIntent = buildPendingIntent(context, Routes.FOCUS, notificationId)
        val rescheduleIntent = buildPendingIntent(context, Routes.JOURNEY, notificationId + 1)
        val skipIntent = buildTaskActionPendingIntent(
            context, TaskActionReceiver.ACTION_SKIP, taskId, notificationId, notificationId + 2
        )

        val text = "Only $minutesRemaining minutes remain for today's planned session."

        val notification = NotificationCompat.Builder(context, NotificationChannels.TASK_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(taskTitle)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(startAnywayIntent)
            .addAction(0, "Start Anyway", startAnywayIntent)
            .addAction(0, "Reschedule", rescheduleIntent)
            .addAction(0, "Skip", skipIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun cancelTaskNotifications(context: Context, taskId: Long) {
        val manager = NotificationManagerCompat.from(context)
        manager.cancel(TASK_PRE_START_ID_BASE + taskId.toInt())
        manager.cancel(TASK_START_ID_BASE + taskId.toInt())
        manager.cancel(TASK_END_WINDOW_ID_BASE + taskId.toInt())
    }
}