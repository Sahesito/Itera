package com.sahe.itera.presentation.screens.attendance

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahe.itera.domain.model.Attendance
import com.sahe.itera.domain.model.AttendanceType
import com.sahe.itera.domain.model.Subject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private sealed interface AttendanceFlow {
    object SubjectList : AttendanceFlow
    data class Detail(val subject: Subject) : AttendanceFlow
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBack: () -> Unit = {},
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var flow by remember { mutableStateOf<AttendanceFlow>(AttendanceFlow.SubjectList) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(when (flow) {
                        is AttendanceFlow.SubjectList -> "Asistencia"
                        is AttendanceFlow.Detail      -> (flow as AttendanceFlow.Detail).subject.name
                    })
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (flow) {
                            is AttendanceFlow.SubjectList -> onBack()
                            is AttendanceFlow.Detail      -> flow = AttendanceFlow.SubjectList
                        }
                    }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (subjects.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sin materias registradas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Agrega materias primero",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (val f = flow) {
                    is AttendanceFlow.SubjectList -> SubjectListStep(
                        subjects  = subjects,
                        viewModel = viewModel,
                        onSubjectClick = { subject -> flow = AttendanceFlow.Detail(subject) }
                    )
                    is AttendanceFlow.Detail -> SubjectDetailStep(
                        subject   = f.subject,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectListStep(
    subjects: List<Subject>,
    viewModel: AttendanceViewModel,
    onSubjectClick: (Subject) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        items(subjects, key = { it.id }) { subject ->
            val attendance by viewModel.getAttendanceForSubject(subject.id)
                .collectAsStateWithLifecycle()
            SubjectAttendanceCard(
                subject    = subject,
                attendance = attendance,
                onClick    = { onSubjectClick(subject) }
            )
        }
    }
}

@Composable
private fun SubjectAttendanceCard(
    subject: Subject,
    attendance: List<Attendance>,
    onClick: () -> Unit
) {
    val absences  = attendance.count { it.type == AttendanceType.ABSENCE }
    val tardiness = attendance.count { it.type == AttendanceType.TARDINESS }

    val subjectColor = runCatching {
        Color(subject.colorHex.toColorInt())
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    val absenceColor = attendanceColor(absences, subject.maxAbsences)
    val tardinessColor = attendanceColor(tardiness, subject.maxTardiness)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(subjectColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(subjectColor)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subject.teacher.isNotBlank()) {
                        Text(
                            text = subject.teacher,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(Icons.Rounded.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AttendanceMiniChip(
                    label   = "Faltas",
                    current = absences,
                    max     = subject.maxAbsences,
                    color   = absenceColor,
                    icon    = Icons.Rounded.EventBusy,
                    modifier = Modifier.weight(1f)
                )
                AttendanceMiniChip(
                    label   = "Tardanzas",
                    current = tardiness,
                    max     = subject.maxTardiness,
                    color   = tardinessColor,
                    icon    = Icons.Rounded.AccessTime,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AttendanceMiniChip(
    label: String,
    current: Int,
    max: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            Column {
                Text(
                    text = if (max > 0) "$current / $max" else "$current",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SubjectDetailStep(
    subject: Subject,
    viewModel: AttendanceViewModel
) {
    val attendance by viewModel.getAttendanceForSubject(subject.id)
        .collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var addType by remember { mutableStateOf(AttendanceType.ABSENCE) }

    val absences  = attendance.count { it.type == AttendanceType.ABSENCE }
    val tardiness = attendance.count { it.type == AttendanceType.TARDINESS }
    val subjectColor = runCatching {
        Color(subject.colorHex.toColorInt())
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    val absenceColor   = attendanceColor(absences, subject.maxAbsences)
    val tardinessColor = attendanceColor(tardiness, subject.maxTardiness)

    if (showAddDialog) {
        AddAttendanceDialog(
            type = addType,
            onDismiss = { showAddDialog = false },
            onConfirm = { date, note ->
                viewModel.insert(Attendance(
                    subjectId = subject.id,
                    type      = addType,
                    date      = date,
                    note      = note
                ))
                showAddDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AttendanceCountCard(
                    label    = "Faltas",
                    current  = absences,
                    max      = subject.maxAbsences,
                    color    = absenceColor,
                    icon     = Icons.Rounded.EventBusy,
                    modifier = Modifier.weight(1f),
                    onAdd    = { addType = AttendanceType.ABSENCE; showAddDialog = true }
                )
                AttendanceCountCard(
                    label    = "Tardanzas",
                    current  = tardiness,
                    max      = subject.maxTardiness,
                    color    = tardinessColor,
                    icon     = Icons.Rounded.AccessTime,
                    modifier = Modifier.weight(1f),
                    onAdd    = { addType = AttendanceType.TARDINESS; showAddDialog = true }
                )
            }
        }

        if (attendance.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sin registros aún",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            items(attendance, key = { it.id }) { record ->
                AttendanceItem(
                    record   = record,
                    onDelete = { viewModel.delete(record) }
                )
            }
        }
    }
}

@Composable
private fun AttendanceCountCard(
    label: String,
    current: Int,
    max: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onAdd: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Rounded.Add, null,
                        tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Text(
                text = if (max > 0) "$current / $max" else "$current",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color.copy(alpha = 0.8f)
            )
            if (max > 0) {
                LinearProgressIndicator(
                    progress = { (current.toFloat() / max).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun AttendanceItem(record: Attendance, onDelete: () -> Unit) {
    val color = if (record.type == AttendanceType.ABSENCE)
        Color(0xFFC6837A) else Color(0xFFE2BF55)
    val label = if (record.type == AttendanceType.ABSENCE) "Falta" else "Tardanza"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.date.format(
                        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", java.util.Locale("es"))
                    ).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (record.note.isNotBlank()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Rounded.Delete, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAttendanceDialog(
    type: AttendanceType,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, String) -> Unit
) {
    val title = if (type == AttendanceType.ABSENCE) "Registrar falta" else "Registrar tardanza"
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = selectedDate.format(
                            DateTimeFormatter.ofPattern("d 'de' MMMM · yyyy", java.util.Locale("es"))
                        )
                    )
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    maxLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDate, note.trim()) }) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun attendanceColor(current: Int, max: Int): Color = when {
    max == 0                        -> Color(0xFF9E9E9E)
    current == 0                    -> Color(0xFF91D19A)
    current >= max                  -> Color(0xFFC6837A)
    current >= (max * 0.7).toInt()  -> Color(0xFFE2BF55)
    else                            -> Color(0xFF91D19A)
}