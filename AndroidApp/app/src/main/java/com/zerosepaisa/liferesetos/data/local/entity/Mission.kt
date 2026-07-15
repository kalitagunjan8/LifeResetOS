package com.zerosepaisa.liferesetos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val statement: String,

    val why: String,

    val isActive: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)