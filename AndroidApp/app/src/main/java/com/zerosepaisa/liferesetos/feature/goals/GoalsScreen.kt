package com.zerosepaisa.liferesetos.feature.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalCategory
import com.zerosepaisa.liferesetos.data.local.entity.enums.GoalPriority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    goalId: Long? = null,
    onGoalSaved: () -> Unit = {}
) {
    val viewModel: GoalsViewModel = viewModel()
    val activeMission by viewModel.activeMission.collectAsState()

    val isEditMode = goalId != null

    var originalGoal by remember { mutableStateOf<Goal?>(null) }
    var isLoading by remember { mutableStateOf(isEditMode) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var why by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(GoalCategory.OTHER) }
    var priority by remember { mutableStateOf(GoalPriority.MEDIUM) }
    var showTitleError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(goalId) {
        if (goalId != null) {
            val goal = viewModel.loadGoal(goalId)
            originalGoal = goal
            goal?.let {
                title = it.title
                description = it.description
                why = it.why
                category = it.category
                priority = it.priority
            }
            isLoading = false
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = if (isEditMode) "Edit Goal" else "Add Goal",
            style = MaterialTheme.typography.headlineMedium
        )

        if (!isEditMode && activeMission == null) {
            Text(
                text = "You need an active Mission before you can add a Goal.",
                style = MaterialTheme.typography.bodyLarge
            )
            return@Column
        }

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (it.isNotBlank()) showTitleError = false
            },
            label = { Text("Title") },
            isError = showTitleError,
            supportingText = {
                if (showTitleError) Text("Title is required")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = why,
            onValueChange = { why = it },
            label = { Text("Why this matters") },
            modifier = Modifier.fillMaxWidth()
        )

        EnumDropdown(
            label = "Category",
            options = GoalCategory.entries.toList(),
            selected = category,
            onSelected = { category = it }
        )

        EnumDropdown(
            label = "Priority",
            options = GoalPriority.entries.toList(),
            selected = priority,
            onSelected = { priority = it }
        )

        Button(
            onClick = {
                if (title.isBlank()) {
                    showTitleError = true
                } else if (isEditMode) {
                    originalGoal?.let { original ->
                        viewModel.updateGoal(
                            original = original,
                            title = title.trim(),
                            description = description.trim(),
                            why = why.trim(),
                            category = category,
                            priority = priority,
                            onSaved = onGoalSaved
                        )
                    }
                } else {
                    viewModel.createGoal(
                        title = title.trim(),
                        description = description.trim(),
                        why = why.trim(),
                        category = category,
                        priority = priority,
                        onSaved = onGoalSaved
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditMode) "Update Goal" else "Save Goal")
        }

        if (isEditMode) {
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Goal")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this Goal?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        originalGoal?.let { goal ->
                            viewModel.deleteGoal(goal, onGoalSaved)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdown(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
