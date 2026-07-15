package com.zerosepaisa.liferesetos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalCategory
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalPriority
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalStatus

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = Mission::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["missionId"])
    ]
)
data class Goal(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Parent Mission
     */
    val missionId: Long,

    /**
     * Goal title
     */
    val title: String,

    /**
     * What this goal is about.
     */
    val description: String = "",

    /**
     * Why this goal helps achieve the Mission.
     */
    val why: String = "",

    /**
     * Business, Health, Learning, etc.
     */
    val category: GoalCategory = GoalCategory.OTHER,

    /**
     * Priority level.
     */
    val priority: GoalPriority = GoalPriority.MEDIUM,

    /**
     * Current lifecycle state.
     */
    val status: GoalStatus = GoalStatus.ACTIVE,

    /**
     * Optional deadline.
     */
    val targetDate: Long? = null,

    /**
     * Creation timestamp.
     */
    val createdAt: Long = System.currentTimeMillis()
)