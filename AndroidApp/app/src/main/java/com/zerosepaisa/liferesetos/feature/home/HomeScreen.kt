package com.zerosepaisa.liferesetos.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onTodaysActionsClick: () -> Unit = {},
    onFocusScoreClick: () -> Unit = {},
    onActiveGoalsClick: () -> Unit = {}
)
{
    val viewModel: HomeViewModel = viewModel()
    val mission by viewModel.activeMission.collectAsState()
    val goals by viewModel.activeGoals.collectAsState()
    val todaysTasks by viewModel.todaysTasks.collectAsState()
    val todaysSessions by viewModel.todaysSessions.collectAsState()

    val completedCount = todaysTasks.count { it.isCompleted }
    val totalCount = todaysTasks.size

    val sessionCount = todaysSessions.size
    val averageFocusScore = if (sessionCount == 0) {
        0
    } else {
        todaysSessions.sumOf { it.focusScore } / sessionCount
    }
    val focusScoreText = if (sessionCount == 0) {
        "No sessions today"
    } else {
        "$sessionCount Sessions · $averageFocusScore% Avg"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Life Reset OS",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Welcome back.",
            style = MaterialTheme.typography.bodyLarge
        )

        DashboardCard(
            icon = Icons.Outlined.TrackChanges,
            title = "Mission",
            value = mission?.title ?: "No Mission Yet"
        )

        DashboardCard(
            icon = Icons.Outlined.TrendingUp,
            title = "Active Goals",
            value = goals.size.toString(),
            onClick = onActiveGoalsClick
        )

        DashboardCard(
            icon = Icons.Outlined.CheckCircle,
            title = "Today's Actions",
            value = "$completedCount / $totalCount Completed",
            onClick = onTodaysActionsClick
        )

        DashboardCard(
            icon = Icons.Outlined.LocalFireDepartment,
            title = "Focus Score",
            value = focusScoreText,
            onClick = onFocusScoreClick
        )

    }

}

@Composable
private fun DashboardCard(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {

    val cardModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        Modifier.fillMaxWidth()
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )

        }

    }

}