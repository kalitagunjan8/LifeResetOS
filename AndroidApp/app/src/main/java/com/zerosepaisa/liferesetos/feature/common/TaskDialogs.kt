package com.zerosepaisa.liferesetos.feature.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zerosepaisa.liferesetos.data.local.entity.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Shared Task dialogs. Extracted from GoalDetailScreen so they can be
 * reused by Journey Workspace (v0.8.5) without duplicating Task CRUD UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var scheduledDate by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) showError = false
                    },
                    label = { Text("Title") },
                    isError = showError,
                    supportingText = {
                        if (showError) Text("Title is required")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TaskDatePickerField(
                    selectedDateMillis = scheduledDate,
                    onDateSelected = { scheduledDate = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(title.trim(), scheduledDate)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdate: (String, Long?) -> Unit,
    onDeleteRequest: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var showError by remember { mutableStateOf(false) }
    var scheduledDate by remember { mutableStateOf(task.scheduledDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) showError = false
                    },
                    label = { Text("Title") },
                    isError = showError,
                    supportingText = {
                        if (showError) Text("Title is required")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TaskDatePickerField(
                    selectedDateMillis = scheduledDate,
                    onDateSelected = { scheduledDate = it }
                )

                OutlinedButton(
                    onClick = onDeleteRequest,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Task")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onUpdate(title.trim(), scheduledDate)
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Shared date-picker field used by both Add and Edit Task dialogs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePickerField(
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }

    Column {
        Text(
            text = "Scheduled for",
            style = MaterialTheme.typography.labelMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = { showPicker = true }) {
                Text(
                    text = selectedDateMillis?.let { dateFormat.format(Date(it)) }
                        ?: "No date set"
                )
            }

            if (selectedDateMillis != null) {
                OutlinedButton(onClick = { onDateSelected(null) }) {
                    Text("Clear")
                }
            }
        }
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}