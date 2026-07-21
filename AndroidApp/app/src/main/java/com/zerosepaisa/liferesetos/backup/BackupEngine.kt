package com.zerosepaisa.liferesetos.backup

import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Mission
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalCategory
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalPriority
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalStatus
import com.zerosepaisa.liferesetos.data.local.entity.enums.SessionStatus
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackupEngine(
    private val missionRepository: MissionRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository
) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    suspend fun buildBackup(appVersion: String): BackupData {
        val activeMission = missionRepository.getActiveMission().first()
        val missions = listOfNotNull(activeMission).map {
            BackupMission(
                id = it.id,
                title = it.title,
                statement = it.statement,
                why = it.why,
                isActive = it.isActive,
                createdAt = it.createdAt
            )
        }

        val goals = goalRepository.getAllGoals().first().map {
            BackupGoal(
                id = it.id,
                missionId = it.missionId,
                title = it.title,
                description = it.description,
                why = it.why,
                category = it.category.name,
                priority = it.priority.name,
                status = it.status.name,
                targetDate = it.targetDate,
                createdAt = it.createdAt
            )
        }

        val tasks = taskRepository.getAllTasks().first().map {
            BackupTask(
                id = it.id,
                goalId = it.goalId,
                title = it.title,
                description = it.description,
                isCompleted = it.isCompleted,
                createdAt = it.createdAt,
                completedAt = it.completedAt,
                scheduledDate = it.scheduledDate,
                startTimeMinutes = it.startTimeMinutes,
                endTimeMinutes = it.endTimeMinutes,
                estimatedDurationMinutes = it.estimatedDurationMinutes
            )
        }

        val sessions = focusSessionRepository.getAllSessions().first().map {
            BackupFocusSession(
                id = it.id,
                taskId = it.taskId,
                plannedDurationSeconds = it.plannedDurationSeconds,
                actualDurationSeconds = it.actualDurationSeconds,
                status = it.status.name,
                focusScore = it.focusScore,
                startedAt = it.startedAt,
                endedAt = it.endedAt
            )
        }

        return BackupData(
            appVersion = appVersion,
            exportedAt = System.currentTimeMillis(),
            missions = missions,
            goals = goals,
            tasks = tasks,
            focusSessions = sessions
        )
    }

    suspend fun exportToJson(appVersion: String): String =
        json.encodeToString(buildBackup(appVersion))

    /**
     * Full replace restore: wipes all existing data, then inserts the
     * backup's data with original IDs preserved. Not run in a DB
     * transaction (BackupEngine depends only on Repositories, never
     * AppDatabase, per the Domain Service pattern) — accepted MVP
     * limitation for this single-user local app.
     */
    suspend fun restoreFromJson(jsonContent: String) {
        val backup = json.decodeFromString<BackupData>(jsonContent)

        // Delete children first, then parents (FK safety)
        focusSessionRepository.deleteAllSessions()
        taskRepository.deleteAllTasks()
        goalRepository.deleteAllGoals()
        missionRepository.deleteAllMissions()

        // Insert parents first, then children
        missionRepository.restoreMissions(
            backup.missions.map {
                Mission(
                    id = it.id,
                    title = it.title,
                    statement = it.statement,
                    why = it.why,
                    isActive = it.isActive,
                    createdAt = it.createdAt
                )
            }
        )

        goalRepository.restoreGoals(
            backup.goals.map {
                Goal(
                    id = it.id,
                    missionId = it.missionId,
                    title = it.title,
                    description = it.description,
                    why = it.why,
                    category = GoalCategory.valueOf(it.category),
                    priority = GoalPriority.valueOf(it.priority),
                    status = GoalStatus.valueOf(it.status),
                    targetDate = it.targetDate,
                    createdAt = it.createdAt
                )
            }
        )

        taskRepository.restoreTasks(
            backup.tasks.map {
                Task(
                    id = it.id,
                    goalId = it.goalId,
                    title = it.title,
                    description = it.description,
                    isCompleted = it.isCompleted,
                    createdAt = it.createdAt,
                    completedAt = it.completedAt,
                    scheduledDate = it.scheduledDate,
                    startTimeMinutes = it.startTimeMinutes,
                    endTimeMinutes = it.endTimeMinutes,
                    estimatedDurationMinutes = it.estimatedDurationMinutes
                )
            }
        )

        focusSessionRepository.restoreSessions(
            backup.focusSessions.map {
                FocusSession(
                    id = it.id,
                    taskId = it.taskId,
                    plannedDurationSeconds = it.plannedDurationSeconds,
                    actualDurationSeconds = it.actualDurationSeconds,
                    status = SessionStatus.valueOf(it.status),
                    focusScore = it.focusScore,
                    startedAt = it.startedAt,
                    endedAt = it.endedAt
                )
            }
        )
    }
}