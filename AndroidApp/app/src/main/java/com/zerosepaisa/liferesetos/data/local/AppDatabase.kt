package com.zerosepaisa.liferesetos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zerosepaisa.liferesetos.data.local.dao.GoalDao
import com.zerosepaisa.liferesetos.data.local.dao.TaskDao
import com.zerosepaisa.liferesetos.data.local.dao.FocusSessionDao
import com.zerosepaisa.liferesetos.data.local.dao.HabitDao
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.data.local.entity.Habit
import com.zerosepaisa.liferesetos.data.local.dao.MissionDao
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.local.dao.HabitCompletionDao
import com.zerosepaisa.liferesetos.data.local.entity.HabitCompletion
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Goal::class,
        Mission::class,
        Task::class,
        FocusSession::class,
        Habit::class,
        HabitCompletion::class
    ],
    version = 6,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun missionDao(): MissionDao
    abstract fun taskDao(): TaskDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun habitDao(): HabitDao

    abstract fun habitCompletionDao(): HabitCompletionDao

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
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            Log.e("AppDatabase", "DESTRUCTIVE MIGRATION TRIGGERED — DB WIPED")
                        }
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.e("AppDatabase", "DB opened. version=${db.version}")
                        }
                    })
                    .build().also { INSTANCE = it }
            }
        }
    }
}