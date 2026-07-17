package com.zerosepaisa.liferesetos.feature.goaldetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerosepaisa.liferesetos.data.local.entity.Goal
import com.zerosepaisa.liferesetos.data.local.entity.Task
import kotlinx.coroutines.launch

@Composable
fun GoalDetailScreen(
    modifier: Modifier = Modifier,
    goalId: Long,
    onEditClick: (Long) -> Unit = {}
) {
    val viewModel: GoalDetailViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var goal by remember { mutableStateOf<Goal?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Initial load + start observing this Goal's Tasks.
    LaunchedEffect(goalId) {
        goal = viewModel.loadGoal(goalId)
        isLoading = false
        viewModel.observeTasks(goalId)
    }

    // Re-fetch the Goal whenever this screen resumes, so edits made on the
    // Goals Edit screen (title, category, priority, etc.) show up when the
    // user navigates back here.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, goalId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch {
                    goal = viewModel.loadGoal(goalId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val currentGoal = goal

    if (currentGoal == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Goal not found.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
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
                    text = currentGoal.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                if (currentGoal.description.isNotBlank()) {
                    Text(
                        text = currentGoal.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "${currentGoal.category} • ${currentGoal.priority} • ${currentGoal.status}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(onClick = { onEditClick(goalId) }) {
                    Text("Edit Goal")
                }
            }

            item {
                Text(
                    text = "✅ Tasks",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { viewModel.toggleComplete(task) }
                    )
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title ->
                viewModel.addTask(goalId, title)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() }
        )

        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
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
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(title.trim())
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
