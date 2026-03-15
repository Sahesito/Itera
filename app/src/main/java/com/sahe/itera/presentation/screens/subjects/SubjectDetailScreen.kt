package com.sahe.itera.presentation.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahe.itera.domain.model.Priority
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.model.Task
import com.sahe.itera.presentation.components.DaySchedule
import com.sahe.itera.presentation.components.DayTimeRow
import java.time.format.DateTimeFormatter

private val DAY_LABELS = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")
private val DAY_SHORT  = listOf("L","M","X","J","V","S","D")

private val COLOR_PALETTE = listOf(
    "#B71C1C","#C62828","#D32F2F","#E53935","#F44336",
    "#880E4F","#AD1457","#C2185B","#E91E63","#F06292",
    "#4A148C","#6A1B9A","#7B1FA2","#9C27B0","#CE93D8",
    "#311B92","#4527A0","#512DA8","#673AB7","#9283DA",
    "#1A237E","#283593","#303F9F","#3949AB","#5C6BC0",
    "#0D47A1","#1565C0","#1976D2","#1E88E5","#5685D5",
    "#01579B","#0277BD","#0288D1","#039BE5","#4FC3F7",
    "#006064","#00838F","#0097A7","#00ACC1","#80DEEA",
    "#1B5E20","#2E7D32","#388E3C","#43A047","#66BB6A",
    "#33691E","#558B2F","#689F38","#7CB342","#91D19A",
    "#827717","#F9A825","#FDD835","#FFEE58","#E2BF55",
    "#E65100","#EF6C00","#F57C00","#FB8C00","#FFA726",
    "#BF360C","#D84315","#E64A19","#F4511E","#C6837A",
    "#3E2723","#4E342E","#5D4037","#6D4C41","#8D6E63",
    "#212121","#424242","#616161","#757575","#9E9E9E",
    "#263238","#37474F","#455A64","#546E7A","#78909C",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: Long,
    onBack: () -> Unit,
    onTaskClick: (Long) -> Unit = {},
    viewModel: SubjectDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(subjectId) { viewModel.load(subjectId) }
    val subject        by viewModel.subject.collectAsStateWithLifecycle()
    val tasks          by viewModel.tasks.collectAsStateWithLifecycle()
    val scheduleBlocks by viewModel.scheduleBlocks.collectAsStateWithLifecycle()

    val pendingCount = tasks.count { !it.isCompleted }
    val subjectColor = subject?.colorHex?.let {
        runCatching { Color(it.toColorInt()) }.getOrNull()
    } ?: MaterialTheme.colorScheme.primary

    var showAddSchedule  by remember { mutableStateOf(false) }
    var showColorPicker  by remember { mutableStateOf(false) }

    if (showColorPicker) {
        ModalBottomSheet(
            onDismissRequest = { showColorPicker = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Cambiar color",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(COLOR_PALETTE) { hex ->
                        val color = runCatching { Color(hex.toColorInt()) }
                            .getOrDefault(Color(0xFF5685D5))
                        val isSelected = subject?.colorHex == hex
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    ) else Modifier
                                )
                                .clickable {
                                    viewModel.updateColor(hex)
                                    showColorPicker = false
                                }
                        )
                    }
                }
            }
        }
    }

    if (showAddSchedule) {
        subject?.let { s ->
            AddScheduleDayDialog(
                subject      = s,
                subjectColor = subjectColor,
                existingDays = scheduleBlocks.map { it.dayOfWeek }.toSet(),
                onDismiss    = { showAddSchedule = false },
                onConfirm    = { dow, sched ->
                    viewModel.insertBlock(ScheduleBlock(
                        subjectId    = s.id,
                        subjectName  = s.name,
                        subjectColor = s.colorHex,
                        dayOfWeek    = dow,
                        startHour    = sched.startHour,
                        endHour      = sched.endHour
                    ))
                    showAddSchedule = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        subject?.let { s ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(subjectColor.copy(alpha = 0.18f))
                                .clickable { showColorPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(subjectColor)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (s.teacher.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(Icons.Rounded.Person, null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(s.teacher,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        IconButton(
                            onClick = { showColorPicker = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Palette,
                                contentDescription = "Cambiar color",
                                tint = subjectColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatChip("Total",       "${tasks.size}",               subjectColor)
                        StatChip("Pendientes",  "$pendingCount",
                            if (pendingCount > 0) Color(0xFFC6837A) else Color(0xFF91D19A))
                        StatChip("Completadas", "${tasks.size - pendingCount}", Color(0xFF91D19A))
                    }
                }

                item {
                    ScheduleSection(
                        blocks       = scheduleBlocks,
                        subjectColor = subjectColor,
                        onAdd        = { showAddSchedule = true },
                        onDelete     = { viewModel.deleteBlock(it) }
                    )
                }

                item { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) }

                if (tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sin tareas asignadas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    item {
                        Text("Tareas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                    items(tasks, key = { it.id }) { task ->
                        SubjectTaskItem(
                            task         = task,
                            subjectColor = subjectColor,
                            onToggle     = { viewModel.toggleComplete(task) },
                            onClick      = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ScheduleSection(
    blocks: List<ScheduleBlock>,
    subjectColor: Color,
    onAdd: () -> Unit,
    onDelete: (ScheduleBlock) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Horario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = onAdd) {
                Icon(Icons.Rounded.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Agregar día")
            }
        }

        if (blocks.isEmpty()) {
            Text("Sin horario configurado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            blocks.sortedBy { it.dayOfWeek }.forEach { block ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = subjectColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = DAY_LABELS[block.dayOfWeek - 1].take(3),
                                style = MaterialTheme.typography.labelMedium,
                                color = subjectColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "${block.startHour}:00 – ${block.endHour}:00",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { onDelete(block) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Delete, null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddScheduleDayDialog(
    subject: Subject,
    subjectColor: Color,
    existingDays: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Int, DaySchedule) -> Unit
) {
    var selectedDow by remember { mutableStateOf<Int?>(null) }
    var schedule    by remember { mutableStateOf(DaySchedule()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Agregar día", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(subjectColor))
                    Text(subject.name, style = MaterialTheme.typography.bodyMedium,
                        color = subjectColor, fontWeight = FontWeight.SemiBold)
                }

                Text("Día", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DAY_SHORT.forEachIndexed { index, label ->
                        val dow = index + 1
                        val isSelected = selectedDow == dow
                        val alreadySet = existingDays.contains(dow)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                alreadySet -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                isSelected -> subjectColor.copy(alpha = 0.2f)
                                else       -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (!alreadySet) Modifier.clickable { selectedDow = dow }
                                    else Modifier
                                )
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = when {
                                    alreadySet -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    isSelected -> subjectColor
                                    else       -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                selectedDow?.let { dow ->
                    DayTimeRow(
                        label       = DAY_LABELS[dow - 1],
                        schedule    = schedule,
                        accentColor = subjectColor,
                        onChanged   = { schedule = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedDow?.let { onConfirm(it, schedule) } },
                enabled = selectedDow != null
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.wrapContentWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun SubjectTaskItem(
    task: Task,
    subjectColor: Color,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Priority.NORMAL     -> Color(0xFF9E9E9E)
        Priority.IMPORTANTE -> Color(0xFFE2BF55)
        Priority.URGENTE    -> Color(0xFFC6837A)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
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
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                task.dueDateTime?.let { dt ->
                    Text(
                        text = dt.format(DateTimeFormatter.ofPattern("d MMM · HH:mm",
                            java.util.Locale("es"))),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}