package com.zerosepaisa.liferesetos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["goalId"])
    ]
)
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Parent Goal. Tasks never belong directly to a Mission (ADR-004).
     */
    val goalId: Long,

    /**
     * Task title, e.g. "Watch Compose lesson".
     */
    val title: String,

    /**
     * Optional extra detail.
     */
    val description: String = "",

    /**
     * Whether this Task has been completed.
     */
    val isCompleted: Boolean = false,

    /**
     * Creation timestamp.
     */
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * Set when the Task is marked complete; cleared if un-marked.
     */
    val completedAt: Long? = null,

    /**
     * The day the user intends to work on this Task (per ADR-011).
     * This is a planning date, not a hard deadline — hence "scheduled"
     * rather than "due".
     */
    val scheduledDate: Long? = null,

    val startTimeMinutes: Int? = null,

    val endTimeMinutes: Int? = null,

    val estimatedDurationMinutes: Int? = null,

    val status: TaskStatus = TaskStatus.PLANNED
)
