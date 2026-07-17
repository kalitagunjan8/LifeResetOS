package com.zerosepaisa.liferesetos.di

import android.content.Context
import androidx.room.Room
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository

class AppContainer(context: Context) {

    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "life_reset_os.db"
    )
        // MVP / pre-release: no users to preserve data for yet.
        // Remove this and add a real Migration before release.
        .fallbackToDestructiveMigration()
        .build()

    val missionRepository = MissionRepository(
        database.missionDao()
    )

    val goalRepository = GoalRepository(
        database.goalDao()
    )

    val taskRepository = TaskRepository(
        database.taskDao()
    )
}