package com.zerosepaisa.liferesetos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(missions: List<Mission>)

    @Query("SELECT * FROM missions WHERE isActive = 1 LIMIT 1")
    fun getActiveMission(): Flow<Mission?>

    @Query("DELETE FROM missions")
    suspend fun deleteAll()
}