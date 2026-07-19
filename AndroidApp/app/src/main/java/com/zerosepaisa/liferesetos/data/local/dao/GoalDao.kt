package com.zerosepaisa.liferesetos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalStatus
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE status = :status ORDER BY createdAt DESC")
    fun getGoalsByStatus(status: GoalStatus): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE missionId = :missionId ORDER BY createdAt DESC")
    fun getGoalsForMission(missionId: Long): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): Goal?

    @Query("UPDATE goals SET status = :status WHERE id = :goalId")
    suspend fun updateStatus(goalId: Long, status: GoalStatus)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<Goal>)

    @Query("DELETE FROM goals")
    suspend fun deleteAll()
}