package com.zerosepaisa.liferesetos.feature.todaysactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerosepaisa.liferesetos.feature.common.TaskRowItem

@Composable
fun TodaysActionsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: TodaysActionsViewModel = viewModel()
    val tasks by viewModel.todaysTasks.collectAsState()

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Today's Actions",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Text(
                text = "$completedCount / $totalCount Completed",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (tasks.isEmpty()) {
            item {
                Text(
                    text = "Nothing scheduled for today.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(tasks, key = { it.id }) { task ->
                TaskRowItem(
                    task = task,
                    onToggle = { viewModel.toggleComplete(task) }
                )
            }
        }
    }
}
