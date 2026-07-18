package com.zerosepaisa.liferesetos.feature.focus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.zerosepaisa.liferesetos.data.local.entity.FocusSession
import com.zerosepaisa.liferesetos.data.local.entity.Task
import com.zerosepaisa.liferesetos.data.local.entity.enums.SessionStatus

@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    onGoToTodaysActions: () -> Unit = {}
) {
    val viewModel: FocusViewModel = viewModel()
    val stage by viewModel.stage.collectAsState()
    val todaysTasks by viewModel.todaysTasks.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()
    val totalSeconds by viewModel.totalSeconds.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val lastSession by viewModel.lastSession.collectAsState()

    when (stage) {

        FocusStage.SELECT_TASK -> {
            SelectTaskStage(
                modifier = modifier,
                tasks = todaysTasks,
                onTaskSelected = { viewModel.selectTask(it) },
                onGoToTodaysActions = onGoToTodaysActions
            )
        }

        FocusStage.SELECT_DURATION -> {
            selectedTask?.let { task ->
                SelectDurationStage(
                    modifier = modifier,
                    task = task,
                    onStart = { minutes -> viewModel.startSession(minutes) }
                )
            }
        }

        FocusStage.RUNNING -> {
            selectedTask?.let { task ->
                RunningStage(
                    modifier = modifier,
                    task = task,
                    totalSeconds = totalSeconds,
                    remainingSeconds = remainingSeconds,
                    onEndEarly = { viewModel.endEarly() }
                )
            }
        }

        FocusStage.RESULT -> {
            lastSession?.let { session ->
                ResultStage(
                    modifier = modifier,
                    session = session,
                    onDone = { viewModel.startOver() }
                )
            }
        }
    }
}

@Composable
private fun SelectTaskStage(
    modifier: Modifier,
    tasks: List<Task>,
    onTaskSelected: (Task) -> Unit,
    onGoToTodaysActions: () -> Unit
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "No tasks scheduled for today.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onGoToTodaysActions) {
                    Text("Go to Today's Actions")
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Focus",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Pick a task to focus on",
            style = MaterialTheme.typography.bodyLarge
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTaskSelected(task) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectDurationStage(
    modifier: Modifier,
    task: Task,
    onStart: (Int) -> Unit
) {
    var customMinutesText by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val presets = listOf(15, 25, 45, 60)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Focusing on",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Choose a duration",
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { minutes ->
                OutlinedButton(onClick = { onStart(minutes) }) {
                    Text(
                        if (minutes == 25) "$minutes* min" else "$minutes min"
                    )
                }
            }
        }

        if (!showCustomInput) {
            OutlinedButton(onClick = { showCustomInput = true }) {
                Text("Custom duration")
            }
        } else {
            OutlinedTextField(
                value = customMinutesText,
                onValueChange = {
                    customMinutesText = it
                    showError = false
                },
                label = { Text("Minutes") },
                isError = showError,
                supportingText = {
                    if (showError) Text("Enter a valid number of minutes")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val minutes = customMinutesText.toIntOrNull()
                    if (minutes == null || minutes <= 0) {
                        showError = true
                    } else {
                        onStart(minutes)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start")
            }
        }
    }
}

@Composable
private fun RunningStage(
    modifier: Modifier,
    task: Task,
    totalSeconds: Int,
    remainingSeconds: Int,
    onEndEarly: () -> Unit
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = timeText,
                style = MaterialTheme.typography.displayLarge
            )

            OutlinedButton(onClick = onEndEarly) {
                Text("End Session")
            }
        }
    }
}

@Composable
private fun ResultStage(
    modifier: Modifier,
    session: FocusSession,
    onDone: () -> Unit
) {
    val resultText = when (session.status) {
        SessionStatus.COMPLETED -> "Completed"
        SessionStatus.ENDED_EARLY -> "Ended Early"
        SessionStatus.BROKEN -> "Broken"
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "${session.actualDurationSeconds / 60} / ${session.plannedDurationSeconds / 60} min",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Focus Score: ${session.focusScore}",
                style = MaterialTheme.typography.titleMedium
            )

            Button(onClick = onDone) {
                Text("Done")
            }
        }
    }
}
