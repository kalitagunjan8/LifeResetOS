package com.zerosepaisa.liferesetos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Habit title, e.g. "Read 10 pages".
     */
    val title: String,

    /**
     * Optional extra detail.
     */
    val description: String = "",

    /**
     * Whether this Habit is currently active.
     */
    val isActive: Boolean = true,

    /**
     * Creation timestamp.
     */
    val createdAt: Long = System.currentTimeMillis()
)