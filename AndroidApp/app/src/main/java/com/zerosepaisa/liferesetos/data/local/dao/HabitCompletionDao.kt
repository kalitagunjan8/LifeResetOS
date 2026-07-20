package com.zerosepaisa.liferesetos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zerosepaisa.liferesetos.data.local.entity.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert
    suspend fun insert(completion: HabitCompletion): Long

    @Delete
    suspend fun delete(completion: HabitCompletion)

    @Query("SELECT * FROM habit_completions WHERE completedDate = :dateMillis")
    fun getCompletionsForDate(dateMillis: Long): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND completedDate = :dateMillis LIMIT 1")
    suspend fun getCompletionForHabitAndDate(habitId: Long, dateMillis: Long): HabitCompletion?

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedDate DESC")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>
}