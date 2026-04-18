package com.sahe.itera.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahe.itera.domain.model.Priority
import com.sahe.itera.domain.model.Task
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onBack: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }
    val task by viewModel.task.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("¿Eliminar tarea?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    task?.let { viewModel.delete(it) { onBack() } }
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        task?.let { t ->

            if (showEditDialog) {
                EditTaskDialog(
                    task = t,
                    onDismiss = { showEditDialog = false },
                    onConfirm = { updated ->
                        viewModel.update(updated)
                        showEditDialog = false
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                t.subjectName?.let { name ->
                    val subjectColor = t.subjectColor?.let {
                        runCatching { Color(it.toColorInt()) }.getOrNull()
                    } ?: MaterialTheme.colorScheme.primary

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(subjectColor)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            color = subjectColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = t.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val priorityColor = when (t.priority) {
                        Priority.NORMAL     -> Color(0xFF9E9E9E)
                        Priority.IMPORTANTE -> Color(0xFFE2BF55)
                        Priority.URGENTE    -> Color(0xFFC6837A)
                    }
                    val priorityLabel = when (t.priority) {
                        Priority.NORMAL     -> "Normal"
                        Priority.IMPORTANTE -> "Importante"
                        Priority.URGENTE    -> "Urgente"
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = priorityLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    if (t.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF91D19A).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Completada",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF91D19A),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                if (t.description.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = t.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                t.dueDateTime?.let { dt ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Fecha y hora",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = dt.format(
                                    DateTimeFormatter.ofPattern(
                                        "EEEE d 'de' MMMM · HH:mm",
                                        java.util.Locale("es")
                                    )
                                ).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (t.hasReminder) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Recordatorio activado 30 min antes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.toggleComplete(t) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (t.isCompleted)
                                MaterialTheme.colorScheme.surfaceVariant
                            else Color(0xFF91D19A)
                        )
                    ) {
                        Icon(
                            if (t.isCompleted) Icons.Rounded.RadioButtonUnchecked
                            else Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = if (t.isCompleted)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (t.isCompleted) "Marcar como pendiente"
                            else "Marcar como completada",
                            color = if (t.isCompleted)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Editar")
                        }

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                )
                            )
                        ) {
                            Icon(Icons.Rounded.Delete, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Eliminar")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var selectedPriority by remember { mutableStateOf(task.priority) }
    var selectedDateTime by remember { mutableStateOf(task.dueDateTime) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = task.dueDateTime
            ?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = task.dueDateTime?.hour ?: 12,
        initialMinute = task.dueDateTime?.minute ?: 0,
        is24Hour = true
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atOffset(java.time.ZoneOffset.UTC).toLocalDate()
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
        title = { Text("Editar tarea", style = MaterialTheme.typography.titleLarge) },
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
                    label = { Text("Descripción") },
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Prioridad",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { priority ->
                        val pColor = when (priority) {
                            Priority.NORMAL     -> Color(0xFF9E9E9E)
                            Priority.IMPORTANTE -> Color(0xFFE2BF55)
                            Priority.URGENTE    -> Color(0xFFC6837A)
                        }
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = {
                                Text(when (priority) {
                                    Priority.NORMAL     -> "Normal"
                                    Priority.IMPORTANTE -> "Importante"
                                    Priority.URGENTE    -> "Urgente"
                                })
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = pColor.copy(alpha = 0.2f),
                                selectedLabelColor = pColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = selectedDateTime?.format(
                            DateTimeFormatter.ofPattern(
                                "d 'de' MMMM · HH:mm",
                                java.util.Locale("es")
                            )
                        ) ?: "Sin fecha"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            task.copy(
                                title       = title.trim(),
                                description = description.trim(),
                                priority    = selectedPriority,
                                dueDateTime = selectedDateTime
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}