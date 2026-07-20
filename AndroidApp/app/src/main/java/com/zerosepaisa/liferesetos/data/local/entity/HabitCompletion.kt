package com.zerosepaisa.liferesetos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["habitId", "completedDate"], unique = true)
    ]
)
data class HabitCompletion(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Habit this completion belongs to.
     */
    val habitId: Long,

    /**
     * Start-of-day epoch millis (device local time) this completion applies
     * to. One row per Habit per calendar day (enforced by unique index) —
     * this is the scalable base table future streak/statistics work will
     * read from, kept separate from Habit itself (per v0.8.2 scope).
     */
    val completedDate: Long,

    /**
     * Timestamp the completion was recorded.
     */
    val completedAt: Long = System.currentTimeMillis()
)