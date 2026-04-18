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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.presentation.components.DaySchedule
import com.sahe.itera.presentation.components.DayTimeRow
import com.sahe.itera.presentation.navigation.Screen
import com.sahe.itera.presentation.theme.AccentBlue

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
fun SubjectsScreen(
    onBack: () -> Unit = {},
    navController: NavController,
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    val subjects         by viewModel.subjects.collectAsStateWithLifecycle()
    val archivedSubjects by viewModel.archivedSubjects.collectAsStateWithLifecycle()
    var showDialog       by remember { mutableStateOf(false) }
    var showArchived     by remember { mutableStateOf(false) }
    var subjectToEdit    by remember { mutableStateOf<Subject?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Materias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (showArchived || archivedSubjects.isNotEmpty()) {
                        IconButton(onClick = { showArchived = !showArchived }) {
                            Icon(
                                Icons.Rounded.Inventory2,
                                contentDescription = if (showArchived) "Ver materias" else "Ver archivados",
                                tint = if (showArchived) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (!showArchived) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Agregar materia")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (showArchived) "Archivados" else "Materias",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            val displayList = if (showArchived) archivedSubjects else subjects
            Text(
                text = "${displayList.size} registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (displayList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (showArchived) "Sin materias archivadas"
                            else "Sin materias",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!showArchived) {
                            Text("Toca + para agregar una",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items = displayList, key = { it.id }) { subject ->
                        SubjectCard(
                            subject    = subject,
                            isArchived = showArchived,
                            onClick    = {
                                if (!showArchived)
                                    navController.navigate(Screen.SubjectDetail.createRoute(subject.id))
                            },
                            onEdit     = { subjectToEdit = subject },
                            onDelete   = { viewModel.delete(subject) },
                            onArchive  = { viewModel.archive(subject) },
                            onUnarchive = { viewModel.unarchive(subject) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddSubjectDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, color, teacher, maxAbsences, maxTardiness, schedule ->
                viewModel.insert(
                    subject = Subject(
                        name         = name,
                        colorHex     = color,
                        teacher      = teacher,
                        maxAbsences  = maxAbsences,
                        maxTardiness = maxTardiness
                    ),
                    onInserted = { subjectId ->
                        schedule.forEach { (dow, times) ->
                            viewModel.insertScheduleBlock(
                                ScheduleBlock(
                                    subjectId    = subjectId,
                                    subjectName  = name,
                                    subjectColor = color,
                                    dayOfWeek    = dow,
                                    startHour    = times.first,
                                    endHour      = times.second
                                )
                            )
                        }
                    }
                )
                showDialog = false
            }
        )
    }

    subjectToEdit?.let { subject ->
        EditSubjectDialog(
            subject   = subject,
            onDismiss = { subjectToEdit = null },
            onConfirm = { updated ->
                viewModel.update(updated)
                subjectToEdit = null
            }
        )
    }
}

@Composable
private fun SubjectCard(
    subject: Subject,
    isArchived: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit
) {
    val cardColor = remember(subject.colorHex) {
        runCatching { Color(subject.colorHex.toColorInt()) }.getOrDefault(AccentBlue)
    }
    var showMenu    by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("¿Eliminar materia?") },
            text = {
                Text(
                    "Se eliminarán también todas las tareas asociadas a \"${subject.name}\". Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirm = false }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(cardColor)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subject.teacher.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(Icons.Rounded.Person, null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(subject.teacher,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                subject.currentAverage?.let {
                    Text("Promedio: ${"%.1f".format(it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isArchived) {
                    subject.archivedAt?.let { archivedAt ->
                        val daysLeft = 30 - ((System.currentTimeMillis() - archivedAt) / (1000 * 60 * 60 * 24)).toInt()
                        Text(
                            "Se elimina en $daysLeft días",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (daysLeft <= 7) Color(0xFFC6837A)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Rounded.MoreVert, "Opciones",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (!isArchived) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            leadingIcon = { Icon(Icons.Rounded.Edit, null) },
                            onClick = { onEdit(); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Archivar") },
                            leadingIcon = { Icon(Icons.Rounded.Inventory2, null) },
                            onClick = { onArchive(); showMenu = false }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Desarchivar") },
                            leadingIcon = { Icon(Icons.Rounded.Unarchive, null) },
                            onClick = { onUnarchive(); showMenu = false }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Delete, null,
                                tint = MaterialTheme.colorScheme.error)
                        },
                        onClick = { showConfirm = true; showMenu = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSubjectDialog(
    subject: Subject,
    onDismiss: () -> Unit,
    onConfirm: (Subject) -> Unit
) {
    var name         by remember { mutableStateOf(subject.name) }
    var teacher      by remember { mutableStateOf(subject.teacher) }
    var maxAbsences  by remember { mutableStateOf(subject.maxAbsences.toString()) }
    var maxTardiness by remember { mutableStateOf(subject.maxTardiness.toString()) }
    var selectedColor by remember { mutableStateOf(subject.colorHex) }
    var showPalette  by remember { mutableStateOf(false) }

    val accentColor = runCatching {
        Color(selectedColor.toColorInt())
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    if (showPalette) {
        ModalBottomSheet(
            onDismissRequest = { showPalette = false },
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
                Text("Elige un color", style = MaterialTheme.typography.titleLarge)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(COLOR_PALETTE) { hex ->
                        val color = runCatching { Color(hex.toColorInt()) }
                            .getOrDefault(Color(0xFF5685D5))
                        val isSelected = selectedColor == hex
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
                                .clickable { selectedColor = hex; showPalette = false }
                        )
                    }
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Editar materia", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Profesor (opcional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(18.dp))
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = maxAbsences,
                        onValueChange = { maxAbsences = it.filter { c -> c.isDigit() } },
                        label = { Text("Máx. faltas") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Rounded.EventBusy, null, modifier = Modifier.size(18.dp))
                        }
                    )
                    OutlinedTextField(
                        value = maxTardiness,
                        onValueChange = { maxTardiness = it.filter { c -> c.isDigit() } },
                        label = { Text("Máx. tardanzas") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Rounded.AccessTime, null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text("Color",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f))
                    FilledTonalButton(
                        onClick = { showPalette = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Palette, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Cambiar")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            subject.copy(
                                name         = name.trim(),
                                teacher      = teacher.trim(),
                                maxAbsences  = maxAbsences.toIntOrNull() ?: 0,
                                maxTardiness = maxTardiness.toIntOrNull() ?: 0,
                                colorHex     = selectedColor
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        color: String,
        teacher: String,
        maxAbsences: Int,
        maxTardiness: Int,
        schedule: List<Pair<Int, Pair<Int, Int>>>
    ) -> Unit
) {
    var name          by remember { mutableStateOf("") }
    var teacher       by remember { mutableStateOf("") }
    var maxAbsences   by remember { mutableStateOf("") }
    var maxTardiness  by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(COLOR_PALETTE.random()) }
    var showPalette   by remember { mutableStateOf(false) }
    var scheduleMap   by remember { mutableStateOf<Map<Int, DaySchedule>>(emptyMap()) }

    val days      = listOf("L","M","X","J","V","S","D")
    val dayLabels = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")
    val accentColor = runCatching {
        Color(selectedColor.toColorInt())
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    if (showPalette) {
        ModalBottomSheet(
            onDismissRequest = { showPalette = false },
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
                Text("Elige un color", style = MaterialTheme.typography.titleLarge)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(COLOR_PALETTE) { hex ->
                        val color = runCatching { Color(hex.toColorInt()) }
                            .getOrDefault(Color(0xFF5685D5))
                        val isSelected = selectedColor == hex
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
                                .clickable { selectedColor = hex; showPalette = false }
                        )
                    }
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Nueva materia", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Profesor (opcional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(18.dp))
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = maxAbsences,
                        onValueChange = { maxAbsences = it.filter { c -> c.isDigit() } },
                        label = { Text("Máx. faltas") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Rounded.EventBusy, null, modifier = Modifier.size(18.dp))
                        }
                    )
                    OutlinedTextField(
                        value = maxTardiness,
                        onValueChange = { maxTardiness = it.filter { c -> c.isDigit() } },
                        label = { Text("Máx. tardanzas") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(Icons.Rounded.AccessTime, null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Color seleccionado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    FilledTonalButton(
                        onClick = { showPalette = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Palette, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Cambiar")
                    }
                }
                Text("Horario de clases (opcional)",
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
                        onChanged = { updated -> scheduleMap = scheduleMap + (dow to updated) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val schedule = scheduleMap.entries.map { (dow, sched) ->
                            dow to (sched.startHour to sched.endHour)
                        }
                        onConfirm(
                            name.trim(), selectedColor, teacher.trim(),
                            maxAbsences.toIntOrNull() ?: 0,
                            maxTardiness.toIntOrNull() ?: 0,
                            schedule
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}