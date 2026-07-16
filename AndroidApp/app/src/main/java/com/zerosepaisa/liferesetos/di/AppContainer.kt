package com.zerosepaisa.liferesetos.di

import android.content.Context
import androidx.room.Room
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository

class AppContainer(context: Context) {

    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "life_reset_os.db"
    ).build()

    val missionRepository = MissionRepository(
        database.missionDao()
    )

    val goalRepository = GoalRepository(
        database.goalDao()
    )
}