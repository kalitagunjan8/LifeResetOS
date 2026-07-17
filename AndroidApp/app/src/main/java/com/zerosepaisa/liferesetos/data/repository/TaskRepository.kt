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
     * Tasks whose scheduledDate falls within the current calendar day
     * (per ADR-011). Tasks with no scheduledDate are excluded.
     */
    fun getTodaysTasks() =
        taskDao.getTasksScheduledBetween(
            DateUtils.startOfToday(),
            DateUtils.endOfToday()
        )

    suspend fun getTaskById(taskId: Long) =
        taskDao.getTaskById(taskId)
}
