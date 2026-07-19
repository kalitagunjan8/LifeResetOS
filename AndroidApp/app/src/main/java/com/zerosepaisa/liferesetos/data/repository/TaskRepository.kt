package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.TaskDao
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.util.DateUtils

class TaskRepository(
    private val taskDao: TaskDao
) {

    suspend fun saveTask(task: Task): Long =
        taskDao.insert(task)

    suspend fun updateTask(task: Task) =
        taskDao.update(task)

    suspend fun deleteTask(task: Task) =
        taskDao.delete(task)

    fun getAllTasks() =
        taskDao.getAllTasks()

    fun getTasksForGoal(goalId: Long) =
        taskDao.getTasksForGoal(goalId)

    /**
     * All Tasks across every Goal belonging to the given Mission.
     * Used by the Progress Engine for Mission Progress %.
     */
    fun getTasksForMission(missionId: Long) =
        taskDao.getTasksForMission(missionId)

    /**
     * Tasks whose scheduledDate falls within the current calendar day
     * (per ADR-011). Tasks with no scheduledDate are excluded.
     */
    fun getTodaysTasks() =
        taskDao.getTasksScheduledBetween(
            DateUtils.startOfToday(),
            DateUtils.endOfToday()
        )

    /**
     * Tasks scheduled within an arbitrary range. Used by the Progress Engine
     * for Weekly/Monthly Completion % without duplicating the day-scoped
     * query above.
     */
    fun getTasksScheduledBetween(startMillis: Long, endMillis: Long) =
        taskDao.getTasksScheduledBetween(startMillis, endMillis)

    suspend fun getTaskById(taskId: Long) =
        taskDao.getTaskById(taskId)

    suspend fun deleteAllTasks() = taskDao.deleteAll()

    suspend fun restoreTasks(tasks: List<Task>) = taskDao.insertAll(tasks)
}
