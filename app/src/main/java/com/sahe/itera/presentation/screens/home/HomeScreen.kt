package com.sahe.itera.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sahe.itera.domain.model.Priority
import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import com.sahe.itera.domain.usecase.task.GetTasksUseCase
import com.sahe.itera.domain.usecase.task.UpdateTaskUseCase
import com.sahe.itera.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class HomeModule(
    val label: String,
    val icon: ImageVector,
    val colorHex: String,
    val route: String,
    val enabled: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    getTasks: GetTasksUseCase,
    private val updateTask: UpdateTaskUseCase
) : ViewModel() {

    fun toggleComplete(task: Task) {
        viewModelScope.launch { updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }
    val hasSubjects = getSubjects()
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val todayTasks = getTasks()
        .map { tasks ->
            val today = java.time.LocalDate.now()
            tasks.filter { task ->
                !task.isCompleted &&
                        task.dueDateTime?.toLocalDate() == today
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val hasSubjects by viewModel.hasSubjects.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()

    val modules = listOf(
        HomeModule("Materias",   Icons.Rounded.School,           "#5685D5", Screen.Subjects.route, true),
        HomeModule("Tareas",     Icons.Rounded.CheckCircle,      "#9283DA", Screen.Tasks.route,    hasSubjects),
        HomeModule("Horario",    Icons.Rounded.CalendarViewWeek, "#91D19A", Screen.Schedule.route, true),
        HomeModule("Calendario", Icons.Rounded.CalendarMonth,    "#E2BF55", Screen.Calendar.route, true),
        HomeModule("Notas",    Icons.Rounded.Grade,    "#C6837A", Screen.Notes.route,    true),
        HomeModule("Ajustes",  Icons.Rounded.Settings, "#78909C", Screen.Settings.route, true),
    )

    val today = remember {
        java.text.SimpleDateFormat("EEEE, d 'de' MMMM", java.util.Locale("es"))
            .format(java.util.Date())
            .replaceFirstChar { it.uppercase() }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Itera",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Módulos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp),
                    userScrollEnabled = false
                ) {
                    items(modules) { module ->
                        HomeModuleItem(
                            module = module,
                            onClick = { navController.navigate(module.route) }
                        )
                    }
                }
            }
        }

        item {
            HomeTodaySummaryCard(
                tasks = todayTasks,
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onToggle = { task -> viewModel.toggleComplete(task) }
            )
        }
    }
}

@Composable
private fun HomeModuleItem(module: HomeModule, onClick: () -> Unit) {
    val color = remember(module.colorHex) {
        runCatching { Color(module.colorHex.toColorInt()) }
            .getOrDefault(Color(0xFF5685D5))
    }
    val finalColor = if (module.enabled) color else color.copy(alpha = 0.35f)
    val cardAlpha = if (module.enabled) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .then(
                if (module.enabled) Modifier.clickable { onClick() }
                else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = cardAlpha)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .alpha(if (module.enabled) 1f else 0.4f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(finalColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = module.label,
                    tint = finalColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = module.label,
                style = MaterialTheme.typography.labelMedium,
                color = if (module.enabled)
                    MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HomeTodaySummaryCard(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onToggle: (Task) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hoy",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (tasks.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${tasks.size} pendiente${if (tasks.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (tasks.isEmpty()) {
                Text(
                    text = "No tienes tareas pendientes para hoy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                tasks.forEach { task ->
                    val subjectColor = task.subjectColor?.let {
                        runCatching { Color(it.toColorInt()) }.getOrNull()
                    }
                    val priorityColor = when (task.priority) {
                        Priority.NORMAL     -> Color(0xFF9E9E9E)
                        Priority.IMPORTANTE -> Color(0xFFE2BF55)
                        Priority.URGENTE    -> Color(0xFFC6837A)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTaskClick(task.id) }
                    ) {

                        IconButton(
                            onClick = { onToggle(task) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Rounded.CheckCircle
                                else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = "Completar",
                                tint = if (task.isCompleted) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            task.subjectName?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        task.dueDateTime?.let { dt ->
                            Text(
                                text = dt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = priorityColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when (task.priority) {
                                    Priority.NORMAL     -> "Normal"
                                    Priority.IMPORTANTE -> "Importante"
                                    Priority.URGENTE    -> "Urgente"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = priorityColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (task != tasks.last()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}