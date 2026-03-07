package com.sahe.itera.presentation.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.presentation.theme.*
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    onBack: () -> Unit = {},
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Materias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Volver")
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
                Icon(Icons.Rounded.Add, contentDescription = "Agregar materia")
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
                text = "Materias",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${subjects.size} registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (subjects.isEmpty()) {
                EmptySubjectsPlaceholder()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items = subjects, key = { it.id }) { subject ->
                        SubjectCard(
                            subject = subject,
                            onDelete = { viewModel.delete(subject) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddSubjectDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, color ->
                viewModel.insert(Subject(name = name, colorHex = color))
                showDialog = false
            }
        )
    }
}

@Composable
private fun SubjectCard(subject: Subject, onDelete: () -> Unit) {
    val cardColor = remember(subject.colorHex) {
        runCatching { Color(subject.colorHex.toColorInt()) }
            .getOrDefault(AccentBlue)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                subject.currentAverage?.let {
                    Text(
                        text = "Promedio: ${"%.1f".format(it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptySubjectsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sin materias", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Toca + para agregar una", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit
) {
    val palette = listOf(
        "#B71C1C", "#C62828", "#D32F2F", "#E53935", "#F44336",
        "#880E4F", "#AD1457", "#C2185B", "#E91E63", "#F06292",
        "#4A148C", "#6A1B9A", "#7B1FA2", "#9C27B0", "#CE93D8",
        "#311B92", "#4527A0", "#512DA8", "#673AB7", "#9283DA",
        "#1A237E", "#283593", "#303F9F", "#3949AB", "#5C6BC0",
        "#0D47A1", "#1565C0", "#1976D2", "#1E88E5", "#5685D5",
        "#01579B", "#0277BD", "#0288D1", "#039BE5", "#4FC3F7",
        "#006064", "#00838F", "#0097A7", "#00ACC1", "#80DEEA",
        "#1B5E20", "#2E7D32", "#388E3C", "#43A047", "#66BB6A",
        "#33691E", "#558B2F", "#689F38", "#7CB342", "#91D19A",
        "#827717", "#F9A825", "#FDD835", "#FFEE58", "#E2BF55",
        "#E65100", "#EF6C00", "#F57C00", "#FB8C00", "#FFA726",
        "#BF360C", "#D84315", "#E64A19", "#F4511E", "#C6837A",
        "#3E2723", "#4E342E", "#5D4037", "#6D4C41", "#8D6E63",
        "#212121", "#424242", "#616161", "#757575", "#9E9E9E",
        "#263238", "#37474F", "#455A64", "#546E7A", "#78909C",
    )

    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(palette.random()) }
    var showPalette by remember { mutableStateOf(false) }

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
                Text(
                    text = "Elige un color",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(palette) { hex ->
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
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {
                                    selectedColor = hex
                                    showPalette = false
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {}
                        }
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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

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
                            .background(
                                runCatching { Color(selectedColor.toColorInt()) }
                                    .getOrDefault(Color.Gray)
                            )
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Color seleccionado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    FilledTonalButton(
                        onClick = { showPalette = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Palette,
                            contentDescription = "Abrir paleta",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Cambiar")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), selectedColor) },
                enabled = name.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}