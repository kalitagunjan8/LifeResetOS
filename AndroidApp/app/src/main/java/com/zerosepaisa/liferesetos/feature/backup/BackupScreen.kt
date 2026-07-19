package com.zerosepaisa.liferesetos.feature.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: BackupViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val restoreState by viewModel.restoreState.collectAsState()

    var pendingFileName by remember { mutableStateOf("") }
    var pendingRestoreUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showRestoreConfirm by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.createBackup(it, pendingFileName) }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingRestoreUri = it
            showRestoreConfirm = true
        }
    }

    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false },
            title = { Text("Replace all data?") },
            text = { Text("Restoring this backup will delete your current Mission, Goals, Tasks and Focus Sessions and replace them with the backup's data. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showRestoreConfirm = false
                    pendingRestoreUri?.let { viewModel.restoreBackup(it) }
                }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Backup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Export your Mission, Goals, Tasks and Focus Sessions to a file you choose.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = uiState !is BackupUiState.InProgress,
            onClick = {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val fileName = "liferesetos_backup_$timestamp.json"
                pendingFileName = fileName
                backupLauncher.launch(fileName)
            }
        ) {
            Text("Create Backup")
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (val state = uiState) {
            is BackupUiState.InProgress -> CircularProgressIndicator()
            is BackupUiState.Success -> Text("Backup saved: ${state.fileName}")
            is BackupUiState.Error -> Text("Backup failed: ${state.message}")
            is BackupUiState.Idle -> {}
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Restore", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Restore from a backup file. This replaces all current data.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = restoreState !is RestoreUiState.InProgress,
            onClick = { restoreLauncher.launch(arrayOf("application/json")) }
        ) {
            Text("Restore Backup")
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (val state = restoreState) {
            is RestoreUiState.InProgress -> CircularProgressIndicator()
            is RestoreUiState.Success -> Text("Restore complete.")
            is RestoreUiState.Error -> Text("Restore failed: ${state.message}")
            is RestoreUiState.Idle -> {}
        }
    }
}