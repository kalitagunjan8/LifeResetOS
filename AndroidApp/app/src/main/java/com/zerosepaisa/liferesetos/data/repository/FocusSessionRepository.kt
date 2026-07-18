package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.FocusSessionDao
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.util.DateUtils

class FocusSessionRepository(
    private val focusSessionDao: FocusSessionDao
) {

    suspend fun saveSession(session: FocusSession): Long =
        focusSessionDao.insert(session)

    fun getSessionsForTask(taskId: Long) =
        focusSessionDao.getSessionsForTask(taskId)

    /**
     * Sessions started today (device local calendar day).
     * Used later to power the Home Dashboard's Focus Score.
     */
    fun getTodaysSessions() =
        focusSessionDao.getSessionsBetween(
            DateUtils.startOfToday(),
            DateUtils.endOfToday()
        )
}
