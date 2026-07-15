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

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
)
{

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
            value = "Build Financial Freedom"
        )

        DashboardCard(
            title = "📈 Active Goals",
            value = "0"
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