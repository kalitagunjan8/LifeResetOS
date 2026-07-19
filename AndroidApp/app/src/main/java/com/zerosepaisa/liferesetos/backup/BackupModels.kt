package com.zerosepaisa.liferesetos.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupMission(
    val id: Long,
    val title: String,
    val statement: String,
    val why: String,
    val isActive: Boolean,
    val createdAt: Long
)

@Serializable
data class BackupGoal(
    val id: Long,
    val missionId: Long,
    val title: String,
    val description: String,
    val why: String,
    val category: String,
    val priority: String,
    val status: String,
    val targetDate: Long? = null,
    val createdAt: Long
)

@Serializable
data class BackupTask(
    val id: Long,
    val goalId: Long,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val completedAt: Long? = null,
    val scheduledDate: Long? = null
)

@Serializable
data class BackupFocusSession(
    val id: Long,
    val taskId: Long,
    val plannedDurationSeconds: Int,
    val actualDurationSeconds: Int,
    val status: String,
    val focusScore: Int,
    val startedAt: Long,
    val endedAt: Long
)

@Serializable
data class BackupData(
    val schemaVersion: Int = 1,
    val appVersion: String,
    val exportedAt: Long,
    val missions: List<BackupMission>,
    val goals: List<BackupGoal>,
    val tasks: List<BackupTask>,
    val focusSessions: List<BackupFocusSession>
)