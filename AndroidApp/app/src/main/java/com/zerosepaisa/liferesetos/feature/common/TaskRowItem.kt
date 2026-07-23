package com.zerosepaisa.liferesetos.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zerosepaisa.liferesetos.data.local.entity.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.zerosepaisa.liferesetos.data.local.entity.enums.TaskStatus
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.animation.animateContentSize


@Composable
fun TaskRowItem(
    task: Task,
    onToggle: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }

    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize()
    } else {
        Modifier.fillMaxWidth()
            .animateContentSize()
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            modifier = Modifier.semantics {
                contentDescription = "Mark \"${task.title}\" as " +
                        if (task.isCompleted) "incomplete" else "complete"
            }
        )

        Column {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge
            )

            task.scheduledDate?.let { millis ->
                Text(
                    text = dateFormat.format(Date(millis)),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            val timeRangeText = when {
                task.startTimeMinutes != null && task.endTimeMinutes != null ->
                    "${formatMinutesOfDay(task.startTimeMinutes)} – ${formatMinutesOfDay(task.endTimeMinutes)}"
                task.startTimeMinutes != null ->
                    formatMinutesOfDay(task.startTimeMinutes)
                else -> null
            }

            timeRangeText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            task.estimatedDurationMinutes?.let { minutes ->
                Text(
                    text = "Est. $minutes min",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            if (task.status == TaskStatus.IN_PROGRESS) {
                Text(
                    text = "In Progress",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
