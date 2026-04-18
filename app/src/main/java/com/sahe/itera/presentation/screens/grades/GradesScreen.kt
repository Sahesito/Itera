package com.sahe.itera.presentation.screens.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.sahe.itera.domain.model.Grade
import com.sahe.itera.domain.model.Subject

private sealed interface GradeFlow {
    object SubjectPicker : GradeFlow
    data class CountPicker(val subject: Subject) : GradeFlow
    data class WeightSetup(val subject: Subject, val count: Int) : GradeFlow
    data class ScoreEntry(val subject: Subject) : GradeFlow
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    onBack: () -> Unit = {},
    initialSubjectId: Long? = null,
    viewModel: GradesViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val overallAverage by viewModel.overallAverage.collectAsStateWithLifecycle()
    var flow by remember { mutableStateOf<GradeFlow>(GradeFlow.SubjectPicker) }

    LaunchedEffect(initialSubjectId, subjects) {
        if (initialSubjectId != null && subjects.isNotEmpty()) {
            val subject = subjects.firstOrNull { it.id == initialSubjectId }
            if (subject != null && flow is GradeFlow.SubjectPicker) {
                flow = GradeFlow.ScoreEntry(subject)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(when (flow) {
                        is GradeFlow.SubjectPicker -> "Notas"
                        is GradeFlow.CountPicker   -> "Nueva configuración"
                        is GradeFlow.WeightSetup   -> "Porcentajes"
                        is GradeFlow.ScoreEntry    -> "Mis notas"
                    })
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (val f = flow) {
                            is GradeFlow.SubjectPicker -> onBack()
                            is GradeFlow.CountPicker   -> flow = GradeFlow.SubjectPicker
                            is GradeFlow.WeightSetup   -> flow = GradeFlow.CountPicker(f.subject)
                            is GradeFlow.ScoreEntry    -> {
                                if (initialSubjectId != null) onBack()
                                else flow = GradeFlow.SubjectPicker
                            }
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val f = flow) {
                is GradeFlow.SubjectPicker -> SubjectPickerStep(
                    subjects = subjects,
                    overallAverage = overallAverage,
                    viewModel = viewModel,
                    onSubjectSelected = { subject -> flow = GradeFlow.ScoreEntry(subject) },
                    onSetupSubject    = { subject -> flow = GradeFlow.CountPicker(subject) }
                )
                is GradeFlow.CountPicker -> CountPickerStep(
                    subject = f.subject,
                    onNext  = { count -> flow = GradeFlow.WeightSetup(f.subject, count) }
                )
                is GradeFlow.WeightSetup -> WeightSetupStep(
                    subject = f.subject,
                    count   = f.count,
                    onNext  = { grades, targetGrade ->
                        viewModel.reconfigureGrades(f.subject, grades, targetGrade)
                        flow = GradeFlow.ScoreEntry(f.subject)
                    }
                )
                is GradeFlow.ScoreEntry -> ScoreEntryStep(
                    subject   = f.subject,
                    viewModel = viewModel,
                    onSetup   = { flow = GradeFlow.CountPicker(f.subject) }
                )
            }
        }
    }
}

@Composable
private fun SubjectPickerStep(
    subjects: List<Subject>,
    overallAverage: Float?,
    viewModel: GradesViewModel,
    onSubjectSelected: (Subject) -> Unit,
    onSetupSubject: (Subject) -> Unit
) {
    if (subjects.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        overallAverage?.let { avg ->
            item { OverallAverageCard(avg) }
        }

        item {
            Text(
                text = "Selecciona una materia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(subjects, key = { it.id }) { subject ->
            val grades by viewModel.getGradesForSubject(subject.id).collectAsStateWithLifecycle()
            val subjectColor = runCatching {
                Color(subject.colorHex.toColorInt())
            }.getOrDefault(MaterialTheme.colorScheme.primary)

            SubjectGradeCard(
                subject = subject,
                grades = grades,
                subjectColor = subjectColor,
                onClick = {
                    if (grades.isEmpty()) onSetupSubject(subject)
                    else onSubjectSelected(subject)
                }
            )
        }
    }
}

@Composable
private fun SubjectGradeCard(
    subject: Subject,
    grades: List<Grade>,
    subjectColor: Color,
    onClick: () -> Unit
) {
    val pendingCount = grades.count { it.score == null }
    val isAtRisk = subject.currentAverage != null &&
            subject.currentAverage < subject.targetGrade

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(subjectColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(subjectColor))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface)
                if (grades.isEmpty()) {
                    Text("Sin configurar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        subject.currentAverage?.let { avg ->
                            val avgColor = when {
                                avg >= 14f -> Color(0xFF91D19A)
                                avg >= 11f -> Color(0xFFE2BF55)
                                else       -> Color(0xFFC6837A)
                            }
                            Surface(shape = RoundedCornerShape(6.dp),
                                color = avgColor.copy(alpha = 0.15f)) {
                                Text("%.1f".format(avg),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold, color = avgColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        if (pendingCount > 0) {
                            Text("$pendingCount pendiente${if (pendingCount > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isAtRisk) {
                            Icon(Icons.Rounded.Warning, null,
                                tint = Color(0xFFC6837A), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Icon(Icons.Rounded.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CountPickerStep(subject: Subject, onNext: (Int) -> Unit) {
    val subjectColor = runCatching {
        Color(subject.colorHex.toColorInt())
    }.getOrDefault(Color(0xFF5685D5))

    var selectedCount by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(subjectColor))
            Text(subject.name, style = MaterialTheme.typography.titleMedium,
                color = subjectColor, fontWeight = FontWeight.SemiBold)
        }

        Text("¿Cuántas evaluaciones tiene este curso?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)

        Text("Máximo 12 evaluaciones",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            (1..12).chunked(4).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { n ->
                        val isSelected = selectedCount == n
                        OutlinedButton(
                            onClick = { selectedCount = n },
                            modifier = Modifier.weight(1f).aspectRatio(1.4f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected)
                                    subjectColor.copy(alpha = 0.15f) else Color.Transparent,
                                contentColor = if (isSelected) subjectColor
                                else MaterialTheme.colorScheme.onSurface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) subjectColor
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            )
                        ) {
                            Text("$n", style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { selectedCount?.let { onNext(it) } },
            enabled = selectedCount != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Siguiente")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.ArrowForward, null, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun WeightSetupStep(subject: Subject,
                            count: Int,
                            onNext: (grades: List<Grade>, targetGrade: Float) -> Unit) {
    val subjectColor = runCatching {
        Color(subject.colorHex.toColorInt())
    }.getOrDefault(Color(0xFF5685D5))

    val defaultNames = remember(count) {
        when (count) {
            1    -> listOf("Final")
            2    -> listOf("Parcial", "Final")
            3    -> listOf("Parcial 1", "Parcial 2", "Final")
            else -> (1 until count).map { "Evaluación $it" } + listOf("Final")
        }
    }

    val names     = remember(count) { defaultNames.map { mutableStateOf(it) }.toMutableList() }
    val weights   = remember(count) { (1..count).map { mutableStateOf("") }.toMutableList() }
    val maxScores = remember(count) { (1..count).map { mutableStateOf("20") }.toMutableList() }
    var targetGrade by remember { mutableStateOf(subject.targetGrade.toInt().toString()) }
    val totalWeight = weights.sumOf { it.value.toFloatOrNull()?.toDouble() ?: 0.0 }.toFloat()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = targetGrade,
                    onValueChange = { targetGrade = it.filter { c -> c.isDigit() } },
                    label = { Text("Nota mínima aprobatoria") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Rounded.Grade, null, modifier = Modifier.size(18.dp))
                    },
                    supportingText = { Text("Ej: 11 en escala de 20, 60 en escala de 100") }
                )
            }

            item {

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(subjectColor))
                    Text(subject.name, style = MaterialTheme.typography.titleMedium,
                        color = subjectColor, fontWeight = FontWeight.SemiBold)
                }
            }
            item {
                Text("Asigna el porcentaje de cada evaluación",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)
            }
            item {
                val totalColor = when {
                    totalWeight == 100f -> Color(0xFF91D19A)
                    totalWeight > 100f  -> Color(0xFFC6837A)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Surface(shape = RoundedCornerShape(12.dp), color = totalColor.copy(alpha = 0.12f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total asignado", style = MaterialTheme.typography.bodyMedium,
                            color = totalColor)
                        Text("${totalWeight.toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = totalColor)
                    }
                }
            }
            itemsIndexed(names) { i, nameState ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = weights[i].value,
                        onValueChange = { weights[i].value = it.filter { c -> c.isDigit() } },
                        label = { Text("%") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxScores[i].value,
                        onValueChange = { maxScores[i].value = it.filter { c -> c.isDigit() } },
                        label = { Text("Nota máx.") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            val allFilled = names.all { it.value.isNotBlank() } &&
                    weights.all { (it.value.toFloatOrNull() ?: 0f) > 0f } &&
                    maxScores.all { (it.value.toFloatOrNull() ?: 0f) > 0f } &&
                    (targetGrade.toFloatOrNull() ?: 0f) > 0f

            Button(
                onClick = {
                    val minGrade = targetGrade.toFloatOrNull() ?: subject.targetGrade
                    onNext(
                        names.mapIndexed { i, n ->
                            Grade(
                                subjectId = subject.id,
                                name      = n.value.trim(),
                                weight    = weights[i].value.toFloat(),
                                score     = null,
                                maxScore  = maxScores[i].value.toFloatOrNull() ?: 20f
                            )
                        },
                        minGrade
                    )
                },
                enabled = allFilled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Guardar y continuar")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.Check, null, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ScoreEntryStep(
    subject: Subject,
    viewModel: GradesViewModel,
    onSetup: () -> Unit
) {
    val grades by viewModel.getGradesForSubject(subject.id).collectAsStateWithLifecycle()
    val subjectColor = runCatching {
        Color(subject.colorHex.toColorInt())
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    val simulatedNeeded = remember(grades, subject.targetGrade) {
        viewModel.simulate(grades, subject.targetGrade, subject.id)
    }
    val isAtRisk = subject.currentAverage != null &&
            subject.currentAverage < subject.targetGrade

    if (grades.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sin evaluaciones configuradas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onSetup, shape = RoundedCornerShape(14.dp)) {
                    Text("Configurar ahora")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(subject.name, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = subjectColor)
                    subject.currentAverage?.let { avg ->
                        Text("Promedio actual: ${"%.2f".format(avg)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isAtRisk) Color(0xFFC6837A)
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                    } ?: Text("Sin promedio aún",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onSetup) {
                    Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reconfigurar")
                }
            }
        }

        if (isAtRisk) {
            item {
                Surface(shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFC6837A).copy(alpha = 0.12f)) {
                    Row(modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Warning, null,
                            tint = Color(0xFFC6837A), modifier = Modifier.size(18.dp))
                        Text("Estás por debajo de la nota mínima (${subject.targetGrade.toInt()})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFC6837A))
                    }
                }
            }
        }

        simulatedNeeded?.let { needed ->
            item {
                Surface(shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF5685D5).copy(alpha = 0.10f)) {
                    Row(modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Calculate, null,
                            tint = Color(0xFF5685D5), modifier = Modifier.size(18.dp))
                        Text("Necesitas ${"%.1f".format(needed)} en pendientes para llegar a ${subject.targetGrade.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5685D5))
                    }
                }
            }
        }

        items(grades, key = { it.id }) { grade ->
            ScoreCard(
                grade = grade,
                onScoreChange = { newScore -> viewModel.update(grade.copy(score = newScore)) }
            )
        }
    }
}

@Composable
private fun ScoreCard(grade: Grade, onScoreChange: (Float?) -> Unit) {
    var editing by remember(grade.id) { mutableStateOf(false) }
    var input by remember(grade.id) { mutableStateOf(grade.score?.toString() ?: "") }

    LaunchedEffect(grade.score) {
        if (!editing) input = ""
    }

    val scoreColor = when {
        grade.score == null                      -> MaterialTheme.colorScheme.onSurfaceVariant
        (grade.score / grade.maxScore) >= 0.7f  -> Color(0xFF91D19A)
        (grade.score / grade.maxScore) >= 0.55f -> Color(0xFFE2BF55)
        else                                     -> Color(0xFFC6837A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(scoreColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (editing) {
                    BasicScoreField(value = input, onValueChange = { input = it }, color = scoreColor)
                } else {
                    Text(
                        text = if (grade.score != null) "%.1f".format(grade.score) else "0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(grade.name, style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text("Peso: ${grade.weight.toInt()}%  ·  Máx: 0–${grade.maxScore.toInt()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (editing) {
                IconButton(
                    onClick = { onScoreChange(input.toFloatOrNull()); editing = false },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Rounded.Check, null,
                        tint = Color(0xFF91D19A), modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { input = grade.score?.toString() ?: ""; editing = false },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Rounded.Close, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp))
                }
            } else {
                IconButton(
                    onClick = {
                        input = ""
                        editing = true
                              },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (grade.score == null) Icons.Rounded.Add else Icons.Rounded.Edit,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BasicScoreField(value: String, onValueChange: (String) -> Unit, color: Color) {
    OutlinedTextField(
        value = value,
        onValueChange = { new ->
            val filtered = new.filter { it.isDigit() || it == '.' }
            if (filtered.count { it == '.' } <= 1) onValueChange(filtered)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.labelLarge.copy(
            textAlign = TextAlign.Center,
            color = color,
            fontWeight = FontWeight.Bold
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.size(56.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Composable
private fun OverallAverageCard(average: Float) {
    val color = when {
        average >= 14f -> Color(0xFF91D19A)
        average >= 11f -> Color(0xFFE2BF55)
        else           -> Color(0xFFC6837A)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Promedio general", style = MaterialTheme.typography.labelMedium, color = color)
                Text("%.2f".format(average),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold, color = color)
            }
            Icon(Icons.Rounded.Grade, null, tint = color, modifier = Modifier.size(40.dp))
        }
    }
}