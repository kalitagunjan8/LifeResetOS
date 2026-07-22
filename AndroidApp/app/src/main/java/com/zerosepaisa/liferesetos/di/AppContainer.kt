package com.zerosepaisa.liferesetos.di

import android.content.Context
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.HabitRepository
import com.zerosepaisa.liferesetos.notifications.NotificationEngine
import com.zerosepaisa.liferesetos.notifications.NotificationScheduler
import com.zerosepaisa.liferesetos.progress.ProgressEngine
import com.zerosepaisa.liferesetos.backup.BackupEngine
import com.zerosepaisa.liferesetos.data.repository.HabitCompletionRepository
import com.zerosepaisa.liferesetos.dailyreview.DailyReviewEngine
import com.zerosepaisa.liferesetos.smartplanning.SmartPlanningEngine

class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val missionRepository = MissionRepository(database.missionDao())
    val goalRepository = GoalRepository(database.goalDao())
    val taskRepository = TaskRepository(database.taskDao())
    val focusSessionRepository = FocusSessionRepository(database.focusSessionDao())
    val habitRepository = HabitRepository(database.habitDao())
    val habitCompletionRepository = HabitCompletionRepository(database.habitCompletionDao())

    val progressEngine = ProgressEngine(
        missionRepository = missionRepository,
        goalRepository = goalRepository,
        taskRepository = taskRepository,
        focusSessionRepository = focusSessionRepository
    )

    val notificationEngine: NotificationEngine by lazy {
        NotificationEngine(
            missionRepository = missionRepository,
            goalRepository = goalRepository,
            taskRepository = taskRepository,
            focusSessionRepository = focusSessionRepository
        )
    }

    val notificationScheduler: NotificationScheduler by lazy {
        NotificationScheduler(context.applicationContext)
    }

    val backupEngine: BackupEngine by lazy {
        BackupEngine(
            missionRepository = missionRepository,
            goalRepository = goalRepository,
            taskRepository = taskRepository,
            focusSessionRepository = focusSessionRepository
        )
    }

    val dailyReviewEngine: DailyReviewEngine by lazy {
        DailyReviewEngine(
            taskRepository = taskRepository,
            focusSessionRepository = focusSessionRepository,
            habitRepository = habitRepository,
            habitCompletionRepository = habitCompletionRepository
        )
    }

    val smartPlanningEngine: SmartPlanningEngine by lazy {
        SmartPlanningEngine(
            taskRepository = taskRepository
        )
    }
}