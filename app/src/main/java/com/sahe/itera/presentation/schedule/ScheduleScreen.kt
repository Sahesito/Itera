package com.sahe.itera.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.presentation.components.DaySchedule
import com.sahe.itera.presentation.components.DayTimeRow
import java.time.LocalDate

private val DAYS = listOf("Lun","Mar","Mié","Jue","Vie","Sáb","Dom")
private val START_HOUR = 5
private val END_HOUR = 24
private val HOUR_HEIGHT = 64.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBack: () -> Unit = {},
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val blocks   by viewModel.blocks.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedBlock by remember { mutableStateOf<ScheduleBlock?>(null) }

    val todayDow = remember { LocalDate.now().dayOfWeek.value }

    if (showAddDialog) {
        AddBlockDialog(
            subjects = subjects,
            onDismiss = { showAddDialog = false },
            onConfirm = { block ->
                viewModel.insert(block)
                showAddDialog = false
            }
        )
    }

    selectedBlock?.let { block ->
        val blockColor = runCatching {
            Color(block.subjectColor.toColorInt())
        }.getOrDefault(MaterialTheme.colorScheme.primary)

        var editMode     by remember { mutableStateOf(false) }
        var moveDayMode  by remember { mutableStateOf(false) }
        var schedule     by remember { mutableStateOf(
            DaySchedule(startHour = block.startHour, endHour = block.endHour)
        )}
        var selectedDow  by remember { mutableStateOf(block.dayOfWeek) }

        AlertDialog(
            onDismissRequest = { selectedBlock = null },
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(blockColor)
                    )
                    Text(block.subjectName, style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Info actual
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = blockColor.copy(alpha = 0.10f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.CalendarMonth, null,
                                tint = blockColor, modifier = Modifier.size(18.dp))
                            Text(
                                text = DAYS[block.dayOfWeek - 1],
                                style = MaterialTheme.typography.bodyMedium,
                                color = blockColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Rounded.Schedule, null,
                                tint = blockColor, modifier = Modifier.size(18.dp))
                            Text(
                                text = "${block.startHour}:00 – ${block.endHour}:00",
                                style = MaterialTheme.typography.bodyMedium,
                                color = blockColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (editMode) {
                        DayTimeRow(
                            label       = "Nueva hora",
                            schedule    = schedule,
                            accentColor = blockColor,
                            onChanged   = { schedule = it }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { editMode = false },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) { Text("Cancelar") }
                            Button(
                                onClick = {
                                    viewModel.delete(block)
                                    viewModel.insert(block.copy(
                                        id        = 0,
                                        startHour = schedule.startHour,
                                        endHour   = schedule.endHour
                                    ))
                                    selectedBlock = null
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) { Text("Guardar") }
                        }
                    }

                    if (moveDayMode) {
                        Text("Mover a",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("L","M","X","J","V","S","D").forEachIndexed { index, label ->
                                val dow = index + 1
                                val isSelected = selectedDow == dow
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) blockColor.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDow = dow }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) blockColor
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { moveDayMode = false; selectedDow = block.dayOfWeek },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) { Text("Cancelar") }
                            Button(
                                onClick = {
                                    viewModel.delete(block)
                                    viewModel.insert(block.copy(id = 0, dayOfWeek = selectedDow))
                                    selectedBlock = null
                                },
                                enabled = selectedDow != block.dayOfWeek,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) { Text("Mover") }
                        }
                    }

                    if (!editMode && !moveDayMode) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { editMode = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Editar hora")
                            }
                            OutlinedButton(
                                onClick = { moveDayMode = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Rounded.SwapHoriz, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Mover día")
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                if (!editMode && !moveDayMode) {
                    TextButton(onClick = {
                        viewModel.delete(block)
                        selectedBlock = null
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Horario") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Rounded.Add, "Agregar bloque")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DayHeader(todayDow = todayDow)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    HourColumn()
                    DAYS.forEachIndexed { index, _ ->
                        val dow = index + 1
                        DayColumn(
                            dayOfWeek = dow,
                            blocks = blocks.filter { it.dayOfWeek == dow },
                            isToday = dow == todayDow,
                            onBlockClick = { selectedBlock = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(todayDow: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp)
    ) {
        DAYS.forEachIndexed { index, day ->
            val dow = index + 1
            val isToday = dow == todayDow
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isToday) {
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}

@Composable
private fun HourColumn() {
    Column(modifier = Modifier.width(40.dp)) {
        (START_HOUR..END_HOUR).forEach { hour ->
            Box(
                modifier = Modifier.height(HOUR_HEIGHT).fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "$hour:00",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 4.dp, top = 2.dp)
                )
            }
        }
    }
}


@Composable
private fun DayColumn(
    dayOfWeek: Int,
    blocks: List<ScheduleBlock>,
    isToday: Boolean,
    onBlockClick: (ScheduleBlock) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalHours = END_HOUR - START_HOUR + 1

    Box(
        modifier = modifier.then(
            if (isToday) Modifier.background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
            ) else Modifier
        )
    ) {
        Column {
            repeat(totalHours) {
                Box(
                    modifier = Modifier
                        .height(HOUR_HEIGHT)
                        .fillMaxWidth()
                        .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )
            }
        }

        blocks.forEach { block ->
            val topOffset  = HOUR_HEIGHT * (block.startHour - START_HOUR)
            val blockHeight = HOUR_HEIGHT * (block.endHour - block.startHour)
            val blockColor = runCatching {
                Color(block.subjectColor.toColorInt())
            }.getOrDefault(MaterialTheme.colorScheme.primary)

            Box(
                modifier = Modifier
                    .padding(top = topOffset, start = 1.dp, end = 1.dp)
                    .height(blockHeight)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(blockColor.copy(alpha = 0.85f))
                    .clickable { onBlockClick(block) }
                    .padding(4.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = block.subjectName,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBlockDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleBlock) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()) }
    var subjectExpanded by remember { mutableStateOf(false) }
    var scheduleMap     by remember { mutableStateOf<Map<Int, DaySchedule>>(emptyMap()) }

    val days      = listOf("L","M","X","J","V","S","D")
    val dayLabels = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")

    val accentColor = selectedSubject?.colorHex?.let {
        runCatching { Color(it.toColorInt()) }.getOrNull()
    } ?: MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Agregar clase", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Selector de materia
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Materia *") },
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
                            val color = runCatching {
                                Color(subject.colorHex.toColorInt())
                            }.getOrDefault(Color.Gray)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
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

                Text("Días",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    days.forEachIndexed { index, label ->
                        val dow = index + 1
                        val isSelected = scheduleMap.containsKey(dow)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    scheduleMap = if (isSelected) scheduleMap - dow
                                    else scheduleMap + (dow to DaySchedule())
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) accentColor
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                scheduleMap.entries.sortedBy { it.key }.forEach { (dow, sched) ->
                    DayTimeRow(
                        label = dayLabels[dow - 1],
                        schedule = sched,
                        accentColor = accentColor,
                        onChanged = { updated ->
                            scheduleMap = scheduleMap + (dow to updated)
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val subject = selectedSubject ?: return@TextButton
                    scheduleMap.entries.sortedBy { it.key }.forEach { (dow, sched) ->
                        onConfirm(ScheduleBlock(
                            subjectId    = subject.id,
                            subjectName  = subject.name,
                            subjectColor = subject.colorHex,
                            dayOfWeek    = dow,
                            startHour    = sched.startHour,
                            endHour      = sched.endHour
                        ))
                    }
                },
                enabled = selectedSubject != null && scheduleMap.isNotEmpty()
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}