package com.zerosepaisa.liferesetos.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
)
{
    val viewModel: HomeViewModel = viewModel()
    val mission by viewModel.activeMission.collectAsState()
    val goals by viewModel.activeGoals.collectAsState()

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
            title = "🎯 Current Mission",
            value = mission?.title ?: "No Mission Yet"
        )

        DashboardCard(
            title = "📈 Active Goals",
            value = goals.size.toString()
        )

        DashboardCard(
            title = "☑ Today's Actions",
            value = "0 / 0 Completed"
        )

        DashboardCard(
            title = "🔥 Focus Score",
            value = "No sessions today"
        )

    }

}

@Composable
private fun DashboardCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )

        }

    }

}