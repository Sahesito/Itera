package com.sahe.itera.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sahe.itera.domain.model.Priority
import com.sahe.itera.domain.model.Task
import com.sahe.itera.presentation.navigation.Screen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onBack: () -> Unit = {},
    navController: NavController,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val tasks       by viewModel.tasks.collectAsStateWithLifecycle()
    val exams       by viewModel.exams.collectAsStateWithLifecycle()
    val taskFilter  by viewModel.taskFilter.collectAsStateWithLifecycle()
    val examFilter  by viewModel.examFilter.collectAsStateWithLifecycle()
    val subjects    by viewModel.subjects.collectAsStateWithLifecycle()
    var selectedTab    by remember { mutableStateOf(0) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var showExamDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showTaskDialog = true
                    else showExamDialog = true
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Rounded.Add, "Nuevo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = MaterialTheme.colorScheme.background,
                contentColor     = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = { Text("Tareas") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = { Text("Exámenes") }
                )
            }

            when (selectedTab) {
                0 -> TasksTab(
                    tasks       = tasks,
                    filter      = taskFilter,
                    onFilter    = { viewModel.setTaskFilter(it) },
                    onToggle    = { viewModel.toggleComplete(it) },
                    onDelete    = { viewModel.delete(it) },
                    onTaskClick = { navController.navigate(Screen.TaskDetail.createRoute(it)) }
                )
                1 -> ExamsTab(
                    exams       = exams,
                    filter      = examFilter,
                    onFilter    = { viewModel.setExamFilter(it) },
                    onToggle    = { viewModel.toggleComplete(it) },
                    onDelete    = { viewModel.delete(it) },
                    onExamClick = { navController.navigate(Screen.TaskDetail.createRoute(it)) }
                )
            }
        }
    }

    if (showTaskDialog) {
        AddTaskDialog(
            subjects  = subjects,
            isExam    = false,
            onDismiss = { showTaskDialog = false },
            onConfirm = { task ->
                viewModel.insert(task)
                showTaskDialog = false
            }
        )
    }

    if (showExamDialog) {
        AddTaskDialog(
            subjects  = subjects,
            isExam    = true,
            onDismiss = { showExamDialog = false },
            onConfirm = { task ->
                viewModel.insert(task)
                showExamDialog = false
            }
        )
    }
}

@Composable
private fun TasksTab(
    tasks: List<Task>,
    filter: TaskFilter,
    onFilter: (TaskFilter) -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            TaskFilter.entries.forEach { f ->
                FilterChip(
                    selected = filter == f,
                    onClick  = { onFilter(f) },
                    label    = {
                        Text(when (f) {
                            TaskFilter.ALL       -> "Todas"
                            TaskFilter.PENDING   -> "Pendientes"
                            TaskFilter.COMPLETED -> "Completadas"
                        })
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        if (tasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sin tareas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Toca + para agregar una",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = tasks, key = { it.id }) { task ->
                    TaskCard(
                        task     = task,
                        onClick  = { onTaskClick(task.id) },
                        onToggle = { onToggle(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExamsTab(
    exams: List<Task>,
    filter: TaskFilter,
    onFilter: (TaskFilter) -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onExamClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            TaskFilter.entries.forEach { f ->
                FilterChip(
                    selected = filter == f,
                    onClick  = { onFilter(f) },
                    label    = {
                        Text(when (f) {
                            TaskFilter.ALL       -> "Todos"
                            TaskFilter.PENDING   -> "Próximos"
                            TaskFilter.COMPLETED -> "Completados"
                        })
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        if (exams.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        when (filter) {
                            TaskFilter.ALL       -> "Sin exámenes"
                            TaskFilter.PENDING   -> "Sin exámenes próximos"
                            TaskFilter.COMPLETED -> "Sin exámenes completados"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    if (filter != TaskFilter.COMPLETED) {
                        Text("Toca + para agregar uno",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = exams, key = { it.id }) { exam ->
                    ExamCard(
                        exam     = exam,
                        onClick  = { onExamClick(exam.id) },
                        onToggle = { onToggle(exam) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onClick: () -> Unit, onToggle: () -> Unit) {
    val subjectColor = task.subjectColor?.let {
        runCatching { Color(it.toColorInt()) }.getOrNull()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                    task.subjectName?.let { name ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            subjectColor?.let { color ->
                                Box(modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color))
                            }
                            Text(name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                IconButton(onClick = { onToggle() }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Rounded.CheckCircle
                        else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = "Completar",
                        tint = if (task.isCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val priorityColor = task.priority.toColor()
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = priorityColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = task.priority.toLabel(),
                        style = MaterialTheme.typography.labelMedium,
                        color = priorityColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                task.dueDateTime?.let { dt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.Schedule, null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = dt.format(DateTimeFormatter.ofPattern(
                                "d MMM · HH:mm", java.util.Locale("es"))),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ExamCard(exam: Task, onClick: () -> Unit, onToggle: () -> Unit) {
    val subjectColor = exam.subjectColor?.let {
        runCatching { Color(it.toColorInt()) }.getOrNull()
    }
    val priorityColor = exam.priority.toColor()

    val daysLeft = exam.dueDateTime?.let {
        java.time.temporal.ChronoUnit.DAYS.between(
            java.time.LocalDate.now(), it.toLocalDate()
        )
    }
    val daysColor = when {
        daysLeft == null -> MaterialTheme.colorScheme.onSurfaceVariant
        daysLeft <= 1L   -> Color(0xFFC6837A)
        daysLeft <= 3L   -> Color(0xFFE2BF55)
        else             -> Color(0xFF91D19A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF5685D5).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Examen",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5685D5),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (exam.isCompleted) TextDecoration.LineThrough else null,
                        color = if (exam.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                    exam.subjectName?.let { name ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            subjectColor?.let { color ->
                                Box(modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color))
                            }
                            Text(name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Icon(Icons.Rounded.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = priorityColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = exam.priority.toLabel(),
                        style = MaterialTheme.typography.labelMedium,
                        color = priorityColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                exam.dueDateTime?.let { dt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Rounded.Schedule, null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = dt.format(DateTimeFormatter.ofPattern(
                                "d MMM · HH:mm", java.util.Locale("es"))),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                if (!exam.isCompleted) {
                    daysLeft?.let {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = daysColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when (daysLeft) {
                                    0L   -> "Hoy"
                                    1L   -> "Mañana"
                                    else -> "En $daysLeft días"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = daysColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF91D19A).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Completado",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF91D19A),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    subjects: List<com.sahe.itera.domain.model.Subject>,
    isExam: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var title            by remember { mutableStateOf("") }
    var description      by remember { mutableStateOf("") }
    var selectedSubject  by remember { mutableStateOf(subjects.firstOrNull()) }
    var selectedPriority by remember { mutableStateOf(Priority.NORMAL) }
    var hasReminder      by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDatePicker   by remember { mutableStateOf(false) }
    var showTimePicker   by remember { mutableStateOf(false) }
    var subjectExpanded  by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        selectedDateTime = date.atTime(
                            selectedDateTime?.hour ?: 23,
                            selectedDateTime?.minute ?: 59
                        )
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Siguiente") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Seleccionar hora") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateTime = selectedDateTime
                        ?.withHour(timePickerState.hour)
                        ?.withMinute(timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                if (isExam) "Nuevo examen" else "Nueva tarea",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        subjects.forEach { subject ->
                            val subjectColor = runCatching {
                                Color(subject.colorHex.toColorInt())
                            }.getOrDefault(Color.Gray)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(subjectColor))
                                        Text(subject.name)
                                    }
                                },
                                onClick = {
                                    selectedSubject = subject
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                Text("Prioridad",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.entries.forEach { priority ->
                        val pColor = priority.toColor()
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick  = { selectedPriority = priority },
                            label    = { Text(priority.toLabel()) },
                            shape    = RoundedCornerShape(10.dp),
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = pColor.copy(alpha = 0.2f),
                                selectedLabelColor     = pColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedButton(
                    onClick  = { showDatePicker = true },
                    shape    = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = selectedDateTime?.format(
                            DateTimeFormatter.ofPattern("d 'de' MMMM · HH:mm",
                                java.util.Locale("es"))
                        ) ?: "Seleccionar fecha y hora"
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Notifications, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Recordatorio 30 min antes",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f))
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it },
                        enabled = selectedDateTime != null
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val subject = selectedSubject ?: return@TextButton
                    if (title.isNotBlank()) {
                        onConfirm(Task(
                            title        = title.trim(),
                            description  = description.trim(),
                            subjectId    = subject.id,
                            subjectName  = subject.name,
                            subjectColor = subject.colorHex,
                            dueDateTime  = selectedDateTime,
                            hasReminder  = hasReminder && selectedDateTime != null,
                            priority     = selectedPriority,
                            isExam       = isExam
                        ))
                    }
                },
                enabled = title.isNotBlank() && selectedSubject != null
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}


private fun Priority.toColor(): Color = when (this) {
    Priority.NORMAL     -> Color(0xFF9E9E9E)
    Priority.IMPORTANTE -> Color(0xFFE2BF55)
    Priority.URGENTE    -> Color(0xFFC6837A)
}

private fun Priority.toLabel(): String = when (this) {
    Priority.NORMAL     -> "Normal"
    Priority.IMPORTANTE -> "Importante"
    Priority.URGENTE    -> "Urgente"
}