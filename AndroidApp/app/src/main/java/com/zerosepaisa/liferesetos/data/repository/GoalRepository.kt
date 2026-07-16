package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.GoalDao
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalStatus

class GoalRepository(
    private val goalDao: GoalDao
) {

    suspend fun saveGoal(goal: Goal): Long =
        goalDao.insert(goal)

    suspend fun updateGoal(goal: Goal) =
        goalDao.update(goal)

    suspend fun deleteGoal(goal: Goal) =
        goalDao.delete(goal)

    fun getAllGoals() =
        goalDao.getAllGoals()

    fun getActiveGoals() =
        goalDao.getGoalsByStatus(GoalStatus.ACTIVE)

    fun getGoalsForMission(missionId: Long) =
        goalDao.getGoalsForMission(missionId)

    suspend fun getGoalById(goalId: Long) =
        goalDao.getGoalById(goalId)

    suspend fun updateStatus(goalId: Long, status: GoalStatus) =
        goalDao.updateStatus(goalId, status)
}
