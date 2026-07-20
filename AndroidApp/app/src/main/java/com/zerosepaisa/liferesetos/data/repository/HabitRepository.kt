package com.zerosepaisa.liferesetos.data.repository

import com.zerosepaisa.liferesetos.data.local.dao.HabitDao
import com.zerosepaisa.liferesetos.data.local.entity.Habit

class HabitRepository(
    private val habitDao: HabitDao
) {

    suspend fun saveHabit(habit: Habit): Long =
        habitDao.insert(habit)

    suspend fun updateHabit(habit: Habit) =
        habitDao.update(habit)

    suspend fun deleteHabit(habit: Habit) =
        habitDao.delete(habit)

    fun getAllHabits() =
        habitDao.getAllHabits()

    suspend fun getHabitById(habitId: Long) =
        habitDao.getHabitById(habitId)
}