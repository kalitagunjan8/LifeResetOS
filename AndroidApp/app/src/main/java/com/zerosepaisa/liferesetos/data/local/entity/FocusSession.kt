package com.zerosepaisa.liferesetos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zerosepaisa.liferesetos.data.local.entity.enums.SessionStatus

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskId"])
    ]
)
data class FocusSession(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Focus Sessions belong to a Task, never directly to a Goal or Mission
     * (Focus Philosophy: Focus Sessions belong to Tasks).
     */
    val taskId: Long,

    val plannedDurationSeconds: Int,

    val actualDurationSeconds: Int,

    val status: SessionStatus,

    /**
     * round(actualDurationSeconds / plannedDurationSeconds * 100), clamped
     * 0-100, per ADR-012. Always 100 for a COMPLETED session.
     */
    val focusScore: Int,

    val startedAt: Long,

    val endedAt: Long
)
