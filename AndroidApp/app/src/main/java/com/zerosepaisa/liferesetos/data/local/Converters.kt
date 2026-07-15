package com.zerosepaisa.liferesetos.data.local

import androidx.room.TypeConverter
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalCategory
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalPriority
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalStatus

class Converters {

    // GoalCategory

    @TypeConverter
    fun fromGoalCategory(category: GoalCategory): String = category.name

    @TypeConverter
    fun toGoalCategory(value: String): GoalCategory =
        GoalCategory.valueOf(value)

    // GoalPriority

    @TypeConverter
    fun fromGoalPriority(priority: GoalPriority): String = priority.name

    @TypeConverter
    fun toGoalPriority(value: String): GoalPriority =
        GoalPriority.valueOf(value)

    // GoalStatus

    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String = status.name

    @TypeConverter
    fun toGoalStatus(value: String): GoalStatus =
        GoalStatus.valueOf(value)
}