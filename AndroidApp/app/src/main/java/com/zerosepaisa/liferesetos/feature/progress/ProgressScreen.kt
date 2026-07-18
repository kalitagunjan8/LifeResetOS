package com.zerosepaisa.liferesetos.feature.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerosepaisa.liferesetos.progress.GlobalProgressSnapshot
import com.zerosepaisa.liferesetos.progress.MissionProgress

/**
 * Read-only Progress screen. Every number displayed here comes directly
 * from ProgressViewModel's ProgressEngine-backed state — no calculation
 * happens in this file (per ADR-013).
 */
@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: ProgressViewModel = viewModel()
    val globalProgress by viewModel.globalProgress.collectAsState()
    val missionProgress by viewModel.missionProgress.collectAsState()
    val goalProgressItems by viewModel.goalProgressItems.collectAsState()

    if (globalProgress == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val progress = globalProgress!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            OverviewSection(progress = progress, missionProgress = missionProgress)
        }

        item {
            FocusSection(progress = progress)
        }

        item {
            StreaksSection(progress = progress)
        }

        item {
            AnalyticsSectionCard(title = "🎯 Goals") {
                if (goalProgressItems.isEmpty()) {
                    Text(
                        text = "No active goals.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    goalProgressItems.forEach { item ->
                        MetricRow(
                            label = item.title,
                            value = "${item.progress.completedTasks} / ${item.progress.totalTasks} · ${item.progress.percent}%"
                        )
                    }
                }
            }
        }

        item {
            TotalsSection(progress = progress)
        }
    }
}

@Composable
private fun OverviewSection(
    progress: GlobalProgressSnapshot,
    missionProgress: MissionProgress?
) {
    AnalyticsSectionCard(title = "📊 Overview") {
        MetricRow(
            label = "Mission Progress",
            value = missionProgress?.let { "${it.completedTasks} / ${it.totalTasks} · ${it.percent}%" }
                ?: "No active mission"
        )
        MetricRow(label = "Today's Completion", value = "${progress.todaysCompletionPercent}%")
        MetricRow(label = "Weekly Completion", value = "${progress.weeklyCompletionPercent}%")
        MetricRow(label = "Monthly Completion", value = "${progress.monthlyCompletionPercent}%")
    }
}

@Composable
private fun FocusSection(progress: GlobalProgressSnapshot) {
    AnalyticsSectionCard(title = "🔥 Focus") {
        MetricRow(label = "Focus Minutes Today", value = "${progress.focusMinutesToday} min")
        MetricRow(label = "Focus Minutes This Week", value = "${progress.focusMinutesThisWeek} min")
        MetricRow(label = "Average Focus Score", value = "${progress.averageFocusScore}%")
    }
}

@Composable
private fun StreaksSection(progress: GlobalProgressSnapshot) {
    AnalyticsSectionCard(title = "🔗 Streaks") {
        MetricRow(label = "Current Streak", value = "${progress.currentStreak} days")
        MetricRow(label = "Longest Streak", value = "${progress.longestStreak} days")
    }
}

@Composable
private fun TotalsSection(progress: GlobalProgressSnapshot) {
    AnalyticsSectionCard(title = "📈 Totals") {
        MetricRow(label = "Completed Tasks", value = "${progress.totalCompletedTasks}")
        MetricRow(label = "Total Focus Sessions", value = "${progress.totalFocusSessions}")
    }
}

/**
 * Reusable section card: a title plus any number of MetricRows (or other
 * content) below it. Used by every section on this screen instead of
 * duplicating Card/Column boilerplate five times.
 */
@Composable
private fun AnalyticsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

/**
 * Reusable label/value row used inside every AnalyticsSectionCard.
 */
@Composable
private fun MetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
