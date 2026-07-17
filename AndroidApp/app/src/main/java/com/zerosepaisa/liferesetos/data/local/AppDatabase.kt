package com.zerosepaisa.liferesetos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zerosepaisa.liferesetos.data.local.dao.GoalDao
import com.zerosepaisa.liferesetos.data.local.dao.TaskDao
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.local.dao.MissionDao
import com.zerosepaisa.liferesetos.data.local.entity.Mission

@Database(
    entities = [
        Goal::class,
        Mission::class,
        Task::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun missionDao(): MissionDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "life_reset_os.db"
                )
                    // MVP / pre-release: no users to preserve data for yet.
                    // Remove this and add a real Migration before release.
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
