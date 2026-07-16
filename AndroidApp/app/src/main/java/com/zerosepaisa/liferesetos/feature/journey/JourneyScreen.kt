package com.zerosepaisa.liferesetos.feature.journey

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerosepaisa.liferesetos.data.local.entity.Goal

@Composable
fun JourneyScreen(
    modifier: Modifier = Modifier,
    onAddGoalClick: () -> Unit = {},
    onGoalClick: (Long) -> Unit = {}
) {
    val viewModel: JourneyViewModel = viewModel()
    val mission by viewModel.activeMission.collectAsState()
    val goals by viewModel.activeGoals.collectAsState()

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
                items(goals) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onGoalClick(goal.id) }
                    )
                }
            }
        }
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
