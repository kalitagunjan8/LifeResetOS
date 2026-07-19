package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.FocusSessionDao
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.util.DateUtils

class FocusSessionRepository(
    private val focusSessionDao: FocusSessionDao
) {

    suspend fun saveSession(session: FocusSession): Long =
        focusSessionDao.insert(session)

    /**
     * All Focus Sessions, all time. Used by the Progress Engine for
     * Total Focus Sessions and all-time Average Focus Score.
     */
    fun getAllSessions() =
        focusSessionDao.getAllSessions()

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

    /**
     * Sessions started within an arbitrary range. Used by the Progress
     * Engine for Focus Minutes This Week without duplicating the
     * day-scoped query above.
     */
    fun getSessionsBetween(startMillis: Long, endMillis: Long) =
        focusSessionDao.getSessionsBetween(startMillis, endMillis)

    suspend fun deleteAllSessions() = focusSessionDao.deleteAll()

    suspend fun restoreSessions(sessions: List<FocusSession>) = focusSessionDao.insertAll(sessions)
}
