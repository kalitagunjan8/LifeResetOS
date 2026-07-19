package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.MissionDao
import com.zerosepaisa.liferesetos.data.local.entity.Mission

class MissionRepository(
    private val missionDao: MissionDao
) {

    suspend fun saveMission(mission: Mission) {
        missionDao.insertMission(mission)
    }

    fun getActiveMission() =
        missionDao.getActiveMission()

    suspend fun deleteAllMissions() = missionDao.deleteAll()

    suspend fun restoreMissions(missions: List<Mission>) = missionDao.insertAll(missions)
}