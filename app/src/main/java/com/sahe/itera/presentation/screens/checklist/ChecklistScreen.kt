package com.sahe.itera.presentation.screens.checklist

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahe.itera.domain.model.ChecklistItem
import com.sahe.itera.domain.model.Subject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onBack: () -> Unit = {},
    viewModel: ChecklistViewModel = hiltViewModel()
) {
    val items    by viewModel.items.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    val pending   = items.filter { !it.isChecked }
    val completed = items.filter { it.isChecked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist") },
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
                onClick = { showDialog = true },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Rounded.Add, "Agregar item")
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sin items en la checklist",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Toca + para agregar uno",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                if (pending.isNotEmpty()) {
                    item {
                        Text("Pendiente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                    items(pending, key = { it.id }) { item ->
                        ChecklistItemCard(
                            item     = item,
                            onToggle = { viewModel.toggle(item) },
                            onDelete = { viewModel.delete(item) }
                        )
                    }
                }
                if (completed.isNotEmpty()) {
                    item {
                        Text("Completado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = if (pending.isNotEmpty()) 8.dp else 0.dp))
                    }
                    items(completed, key = { it.id }) { item ->
                        ChecklistItemCard(
                            item     = item,
                            onToggle = { viewModel.toggle(item) },
                            onDelete = { viewModel.delete(item) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddChecklistItemDialog(
            subjects  = subjects,
            onDismiss = { showDialog = false },
            onConfirm = { item ->
                viewModel.insert(item)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ChecklistItemCard(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val subjectColor = item.subjectColor?.let {
        runCatching { Color(it.toColorInt()) }.getOrNull()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.material,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    color = if (item.isChecked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    item.subjectName?.let { name ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            subjectColor?.let { color ->
                                Box(modifier = Modifier
                                    .size(6.dp).clip(CircleShape).background(color))
                            }
                            Text(name, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    item.dueDate?.let { date ->
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern(
                                "d MMM", java.util.Locale("es"))),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (date.isBefore(LocalDate.now()) && !item.isChecked)
                                Color(0xFFC6837A)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    item.linkedExpositionId?.let {
                        Surface(shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF9283DA).copy(alpha = 0.15f)) {
                            Text("Exposición",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9283DA),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
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
private fun AddChecklistItemDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (ChecklistItem) -> Unit
) {
    var material        by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var subjectExpanded by remember { mutableStateOf(false) }
    var selectedDate    by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker  by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atOffset(java.time.ZoneOffset.UTC).toLocalDate()
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
        title = { Text("Nuevo material", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = material,
                    onValueChange = { material = it },
                    label = { Text("Material *") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "Sin curso",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso (opcional)") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin curso") },
                            onClick = { selectedSubject = null; subjectExpanded = false }
                        )
                        subjects.forEach { subject ->
                            val color = runCatching {
                                Color(subject.colorHex.toColorInt())
                            }.getOrDefault(Color.Gray)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Box(modifier = Modifier
                                            .size(10.dp).clip(CircleShape).background(color))
                                        Text(subject.name)
                                    }
                                },
                                onClick = { selectedSubject = subject; subjectExpanded = false }
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(selectedDate?.format(
                        DateTimeFormatter.ofPattern("d 'de' MMMM", java.util.Locale("es"))
                    ) ?: "Fecha para llevarlo (opcional)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (material.isNotBlank()) {
                        onConfirm(ChecklistItem(
                            material    = material.trim(),
                            subjectId   = selectedSubject?.id,
                            subjectName = selectedSubject?.name,
                            subjectColor = selectedSubject?.colorHex,
                            dueDate     = selectedDate
                        ))
                    }
                },
                enabled = material.isNotBlank()
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}