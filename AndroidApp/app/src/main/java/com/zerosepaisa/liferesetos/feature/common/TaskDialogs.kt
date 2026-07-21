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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState

/**
 * Shared Task dialogs. Extracted from GoalDetailScreen so they can be
 * reused by Journey Workspace (v0.8.5) without duplicating Task CRUD UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long?, Int?, Int?, Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var scheduledDate by remember { mutableStateOf<Long?>(null) }
    var startTimeMinutes by remember { mutableStateOf<Int?>(null) }
    var endTimeMinutes by remember { mutableStateOf<Int?>(null) }
    var estimatedDurationText by remember { mutableStateOf("") }

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

                TaskTimePickerField(
                    label = "Start time",
                    selectedMinutes = startTimeMinutes,
                    onTimeSelected = { startTimeMinutes = it }
                )

                TaskTimePickerField(
                    label = "End time",
                    selectedMinutes = endTimeMinutes,
                    onTimeSelected = { endTimeMinutes = it }
                )

                OutlinedTextField(
                    value = estimatedDurationText,
                    onValueChange = { estimatedDurationText = it },
                    label = { Text("Estimated duration (min, optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(
                            title.trim(),
                            scheduledDate,
                            startTimeMinutes,
                            endTimeMinutes,
                            estimatedDurationText.toIntOrNull()
                        )
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
    onUpdate: (String, Long?, Int?, Int?, Int?) -> Unit,
    onDeleteRequest: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var showError by remember { mutableStateOf(false) }
    var scheduledDate by remember { mutableStateOf(task.scheduledDate) }
    var startTimeMinutes by remember { mutableStateOf(task.startTimeMinutes) }
    var endTimeMinutes by remember { mutableStateOf(task.endTimeMinutes) }
    var estimatedDurationText by remember {
        mutableStateOf(task.estimatedDurationMinutes?.toString() ?: "")
    }

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

                TaskTimePickerField(
                    label = "Start time",
                    selectedMinutes = startTimeMinutes,
                    onTimeSelected = { startTimeMinutes = it }
                )

                TaskTimePickerField(
                    label = "End time",
                    selectedMinutes = endTimeMinutes,
                    onTimeSelected = { endTimeMinutes = it }
                )

                OutlinedTextField(
                    value = estimatedDurationText,
                    onValueChange = { estimatedDurationText = it },
                    label = { Text("Estimated duration (min, optional)") },
                    modifier = Modifier.fillMaxWidth()
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
                        onUpdate(
                            title.trim(),
                            scheduledDate,
                            startTimeMinutes,
                            endTimeMinutes,
                            estimatedDurationText.toIntOrNull()
                        )
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

/**
 * Shared start/end time picker field used by both Add and Edit Task
 * dialogs (per ADR-015 Scheduling Philosophy: Start Time / End Time are
 * independent optional fields).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTimePickerField(
    label: String,
    selectedMinutes: Int?,
    onTimeSelected: (Int?) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = { showPicker = true }) {
            Text(selectedMinutes?.let { formatMinutesOfDay(it) } ?: label)
        }

        if (selectedMinutes != null) {
            OutlinedButton(onClick = { onTimeSelected(null) }) {
                Text("Clear")
            }
        }
    }

    if (showPicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedMinutes?.let { it / 60 } ?: 9,
            initialMinute = selectedMinutes?.let { it % 60 } ?: 0,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(label) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(timePickerState.hour * 60 + timePickerState.minute)
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
        )
    }
}

/**
 * Formats minutes-since-midnight as a 12-hour clock string, e.g. 570 -> "9:30 AM".
 */
fun formatMinutesOfDay(minutes: Int): String {
    val hour = minutes / 60
    val minute = minutes % 60
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
}