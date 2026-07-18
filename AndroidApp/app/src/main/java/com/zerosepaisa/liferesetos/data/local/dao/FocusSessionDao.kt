package com.zerosepaisa.liferesetos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insert(session: FocusSession): Long

    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE taskId = :taskId ORDER BY startedAt DESC")
    fun getSessionsForTask(taskId: Long): Flow<List<FocusSession>>

    @Query(
        "SELECT * FROM focus_sessions WHERE startedAt >= :startMillis AND startedAt <= :endMillis ORDER BY startedAt DESC"
    )
    fun getSessionsBetween(startMillis: Long, endMillis: Long): Flow<List<FocusSession>>
}
