package com.zerosepaisa.liferesetos.feature.journey

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Habit
import com.zerosepaisa.liferesetos.data.local.entity.Task
import androidx.compose.material3.Checkbox
import androidx.compose.ui.text.style.TextDecoration
import com.zerosepaisa.liferesetos.streaks.HabitStreak
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import com.zerosepaisa.liferesetos.feature.common.EditTaskDialog
import com.zerosepaisa.liferesetos.feature.common.TaskDatePickerField
import com.zerosepaisa.liferesetos.feature.common.TaskRowItem
import java.util.Locale
import com.zerosepaisa.liferesetos.feature.common.TaskTimePickerField

@Composable
fun JourneyScreen(
    modifier: Modifier = Modifier,
    onAddGoalClick: () -> Unit = {},
    onGoalClick: (Long) -> Unit = {}
) {
    val viewModel: JourneyViewModel = viewModel()
    val mission by viewModel.activeMission.collectAsState()
    val goals by viewModel.activeGoals.collectAsState()
    val taskItems by viewModel.taskItems.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val todaysCompletedHabitIds by viewModel.todaysCompletedHabitIds.collectAsState()
    val habitStreaks by viewModel.habitStreaks.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var deleteConfirmTask by remember { mutableStateOf<Task?>(null) }

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var deleteConfirmHabit by remember { mutableStateOf<Habit?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGoalClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add Goal")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    text = "Journey",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "🎯 Mission",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = mission?.title ?: "No Mission Yet",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                Text(
                    text = "📈 Goals",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (goals.isEmpty()) {
                item {
                    Text(
                        text = "No goals yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(goals, key = { "goal_${it.id}" }) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onGoalClick(goal.id) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔁 Habits",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(onClick = { showAddHabitDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Habit")
                    }
                }
            }

            if (habits.isEmpty()) {
                item {
                    Text(
                        text = "No habits yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(habits, key = { "habit_${it.id}" }) { habit ->
                    HabitCard(
                        habit = habit,
                        isCompletedToday = todaysCompletedHabitIds.contains(habit.id),
                        streak = habitStreaks[habit.id],
                        onClick = { editingHabit = habit },
                        onToggleComplete = { viewModel.toggleHabitCompletion(habit) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✅ Tasks",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (goals.isNotEmpty()) {
                        IconButton(onClick = { showAddTaskDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Task")
                        }
                    }
                }
            }

            if (goals.isEmpty()) {
                item {
                    Text(
                        text = "Add a Goal first to create Tasks.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (taskItems.isEmpty()) {
                item {
                    Text(
                        text = "No tasks yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(taskItems, key = { "task_${it.task.id}" }) { item ->
                    JourneyTaskRow(
                        item = item,
                        onToggle = { viewModel.toggleTaskComplete(item.task) },
                        onClick = { editingTask = item.task }
                    )
                }
            }
        }
    }

    // ---- Task dialogs ----

    if (showAddTaskDialog) {
        AddTaskWithGoalDialog(
            goals = goals,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { goalId, title, scheduledDate, startTime, endTime, estimatedDuration ->
                viewModel.addTask(goalId, title, scheduledDate, startTime, endTime, estimatedDuration)
                showAddTaskDialog = false
            }
        )
    }

    editingTask?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { editingTask = null },
            onUpdate = { newTitle, scheduledDate, startTime, endTime, estimatedDuration ->
                viewModel.updateTask(task, newTitle, scheduledDate, startTime, endTime, estimatedDuration)
                editingTask = null
            },
            onDeleteRequest = {
                deleteConfirmTask = task
                editingTask = null
            }
        )
    }

    deleteConfirmTask?.let { task ->
        AlertDialog(
            onDismissRequest = { deleteConfirmTask = null },
            title = { Text("Delete this Task?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(task)
                        deleteConfirmTask = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteConfirmTask = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---- Habit dialogs ----

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { title, description, reminderEnabled, reminderHour, reminderMinute ->
                viewModel.createHabit(title, description, reminderEnabled, reminderHour, reminderMinute)
                showAddHabitDialog = false
            }
        )
    }

    editingHabit?.let { habit ->
        EditHabitDialog(
            habit = habit,
            onDismiss = { editingHabit = null },
            onUpdate = { title, description, isActive, reminderEnabled, reminderHour, reminderMinute ->
                viewModel.updateHabit(
                    habit, title, description, isActive,
                    reminderEnabled, reminderHour, reminderMinute
                )
                editingHabit = null
            },
            onDeleteRequest = {
                deleteConfirmHabit = habit
                editingHabit = null
            }
        )
    }

    deleteConfirmHabit?.let { habit ->
        AlertDialog(
            onDismissRequest = { deleteConfirmHabit = null },
            title = { Text("Delete this Habit?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHabit(habit)
                        deleteConfirmHabit = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteConfirmHabit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${goal.category} • ${goal.priority} • ${goal.status}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Task row for the Journey Workspace's flat Tasks section. Shows which
 * Goal the Task belongs to (since Tasks are no longer nested under their
 * Goal card here), reusing the shared TaskRowItem for the title/checkbox/
 * scheduledDate presentation.
 */
@Composable
private fun JourneyTaskRow(
    item: JourneyTaskItem,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                text = item.goalTitle,
                style = MaterialTheme.typography.labelSmall
            )
            TaskRowItem(
                task = item.task,
                onToggle = onToggle,
                onClick = onClick
            )
        }
    }
}

/**
 * Add Task dialog for the Journey Tasks section. Unlike GoalDetail (where
 * the Goal is already known from context, ADR-010), Journey's flat Tasks
 * section requires picking which Goal a new Task belongs to (Task.goalId
 * is required — ADR-004). Title/date fields reuse TaskDatePickerField.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskWithGoalDialog(
    goals: List<Goal>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, Long?, Int?, Int?, Int?) -> Unit
) {
    var selectedGoal by remember { mutableStateOf(goals.first()) }
    var title by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var scheduledDate by remember { mutableStateOf<Long?>(null) }
    var startTimeMinutes by remember { mutableStateOf<Int?>(null) }
    var endTimeMinutes by remember { mutableStateOf<Int?>(null) }
    var estimatedDurationText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedGoal.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        goals.forEach { goal ->
                            DropdownMenuItem(
                                text = { Text(goal.title) },
                                onClick = {
                                    selectedGoal = goal
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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
                            selectedGoal.id,
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

@Composable
private fun HabitCard(
    habit: Habit,
    isCompletedToday: Boolean,
    streak: HabitStreak?,
    onClick: () -> Unit = {},
    onToggleComplete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { onToggleComplete() }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (habit.reminderEnabled) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Reminder enabled",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when {
                        isCompletedToday -> "Completed today"
                        !habit.isActive -> "Inactive"
                        else -> "Active"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (streak != null && streak.currentStreak > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "🔥 ${streak.currentStreak} day streak",
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (streak.longestStreak > streak.currentStreak) {
                        Text(
                            text = "Best: ${streak.longestStreak} days",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean, Int?, Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderHour by remember { mutableStateOf<Int?>(null) }
    var reminderMinute by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Habit") },
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

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                ReminderSection(
                    reminderEnabled = reminderEnabled,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute,
                    onReminderEnabledChange = { reminderEnabled = it },
                    onTimeSelected = { hour, minute ->
                        reminderHour = hour
                        reminderMinute = minute
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(title.trim(), description.trim(), reminderEnabled, reminderHour, reminderMinute)
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
private fun EditHabitDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onUpdate: (String, String, Boolean, Boolean, Int?, Int?) -> Unit,
    onDeleteRequest: () -> Unit
) {
    var title by remember { mutableStateOf(habit.title) }
    var description by remember { mutableStateOf(habit.description) }
    var isActive by remember { mutableStateOf(habit.isActive) }
    var showError by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(habit.reminderEnabled) }
    var reminderHour by remember { mutableStateOf(habit.reminderHour) }
    var reminderMinute by remember { mutableStateOf(habit.reminderMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Habit") },
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

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isActive) "Active" else "Inactive")
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }

                ReminderSection(
                    reminderEnabled = reminderEnabled,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute,
                    onReminderEnabledChange = { reminderEnabled = it },
                    onTimeSelected = { hour, minute ->
                        reminderHour = hour
                        reminderMinute = minute
                    }
                )

                OutlinedButton(
                    onClick = onDeleteRequest,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Habit")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onUpdate(title.trim(), description.trim(), isActive, reminderEnabled, reminderHour, reminderMinute)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderSection(
    reminderEnabled: Boolean,
    reminderHour: Int?,
    reminderMinute: Int?,
    onReminderEnabledChange: (Boolean) -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Remind me")
        Switch(
            checked = reminderEnabled,
            onCheckedChange = onReminderEnabledChange
        )
    }

    if (reminderEnabled) {
        OutlinedButton(onClick = { showTimePicker = true }) {
            Text(formatReminderTime(reminderHour, reminderMinute))
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderHour ?: 9,
            initialMinute = reminderMinute ?: 0,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Reminder Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatReminderTime(hour: Int?, minute: Int?): String {
    if (hour == null || minute == null) return "Set time"
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
}