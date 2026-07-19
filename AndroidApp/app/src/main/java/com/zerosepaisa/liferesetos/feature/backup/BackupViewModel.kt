package com.zerosepaisa.liferesetos.feature.backup

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.backup.BackupEngine
import com.zerosepaisa.liferesetos.data.local.AppDatabase
import com.zerosepaisa.liferesetos.data.repository.FocusSessionRepository
import com.zerosepaisa.liferesetos.data.repository.GoalRepository
import com.zerosepaisa.liferesetos.data.repository.MissionRepository
import com.zerosepaisa.liferesetos.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter

sealed class BackupUiState {
    object Idle : BackupUiState()
    object InProgress : BackupUiState()
    data class Success(val fileName: String) : BackupUiState()
    data class Error(val message: String) : BackupUiState()
}

sealed class RestoreUiState {
    object Idle : RestoreUiState()
    object InProgress : RestoreUiState()
    object Success : RestoreUiState()
    data class Error(val message: String) : RestoreUiState()
}

class BackupViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val missionRepository = MissionRepository(
        AppDatabase.getInstance(application).missionDao()
    )

    private val goalRepository = GoalRepository(
        AppDatabase.getInstance(application).goalDao()
    )

    private val taskRepository = TaskRepository(
        AppDatabase.getInstance(application).taskDao()
    )

    private val focusSessionRepository = FocusSessionRepository(
        AppDatabase.getInstance(application).focusSessionDao()
    )

    private val backupEngine = BackupEngine(
        missionRepository = missionRepository,
        goalRepository = goalRepository,
        taskRepository = taskRepository,
        focusSessionRepository = focusSessionRepository
    )

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _restoreState = MutableStateFlow<RestoreUiState>(RestoreUiState.Idle)
    val restoreState: StateFlow<RestoreUiState> = _restoreState.asStateFlow()

    fun createBackup(targetUri: Uri, fileName: String) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.InProgress
            try {
                val appVersion = getApplication<Application>()
                    .packageManager
                    .getPackageInfo(getApplication<Application>().packageName, 0)
                    .versionName ?: "unknown"

                val jsonContent = backupEngine.exportToJson(appVersion)

                withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver
                        .openOutputStream(targetUri)?.use { stream ->
                            OutputStreamWriter(stream).use { it.write(jsonContent) }
                        } ?: throw IllegalStateException("Unable to open output stream")
                }

                _uiState.value = BackupUiState.Success(fileName)
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error(e.message ?: "Backup failed")
            }
        }
    }

    fun restoreBackup(sourceUri: Uri) {
        viewModelScope.launch {
            _restoreState.value = RestoreUiState.InProgress
            try {
                val jsonContent = withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver
                        .openInputStream(sourceUri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalStateException("Unable to open input stream")
                }

                backupEngine.restoreFromJson(jsonContent)
                _restoreState.value = RestoreUiState.Success
            } catch (e: Exception) {
                _restoreState.value = RestoreUiState.Error(e.message ?: "Restore failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = BackupUiState.Idle
    }

    fun resetRestoreState() {
        _restoreState.value = RestoreUiState.Idle
    }
}