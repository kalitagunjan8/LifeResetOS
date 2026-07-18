package com.zerosepaisa.liferesetos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zerosepaisa.liferesetos.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE goalId = :goalId ORDER BY createdAt DESC")
    fun getTasksForGoal(goalId: Long): Flow<List<Task>>

    /**
     * All Tasks under any Goal belonging to the given Mission (via join).
     * Used by the Progress Engine for Mission Progress %.
     */
    @Query(
        """
        SELECT tasks.* FROM tasks
        INNER JOIN goals ON tasks.goalId = goals.id
        WHERE goals.missionId = :missionId
        ORDER BY tasks.createdAt DESC
        """
    )
    fun getTasksForMission(missionId: Long): Flow<List<Task>>

    @Query(
        "SELECT * FROM tasks WHERE scheduledDate >= :startMillis AND scheduledDate <= :endMillis ORDER BY createdAt DESC"
    )
    fun getTasksScheduledBetween(startMillis: Long, endMillis: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?
}
