package com.sahe.itera.presentation.screens.home

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.sahe.itera.presentation.navigation.Screen

private data class HomeModule(
    val label: String,
    val icon: ImageVector,
    val colorHex: String,
    val route: String
)

@Composable
fun HomeScreen(navController: NavController) {
    val today = remember {
        java.text.SimpleDateFormat("EEEE, d 'de' MMMM", java.util.Locale("es"))
            .format(java.util.Date())
            .replaceFirstChar { it.uppercase() }
    }

    val modules = listOf(
        HomeModule("Materias",   Icons.Rounded.School,           "#5685D5", Screen.Subjects.route),
        HomeModule("Tareas",     Icons.Rounded.CheckCircle,      "#9283DA", Screen.Tasks.route),
        HomeModule("Horario",    Icons.Rounded.CalendarViewWeek, "#91D19A", Screen.Schedule.route),
        HomeModule("Calendario", Icons.Rounded.CalendarMonth,    "#E2BF55", Screen.Calendar.route),
        HomeModule("Notas",      Icons.Rounded.Grade,            "#C6837A", Screen.Subjects.route),
        HomeModule("Ajustes",    Icons.Rounded.Settings,         "#78909C", Screen.Subjects.route),
    )

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
            HomeTodaySummaryCard()
        }
    }
}

@Composable
private fun HomeModuleItem(module: HomeModule, onClick: () -> Unit) {
    val color = remember(module.colorHex) {
        runCatching { Color(module.colorHex.toColorInt()) }
            .getOrDefault(Color(0xFF5685D5))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = module.label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = module.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HomeTodaySummaryCard() {
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
            Text(
                text = "Resumen de hoy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "No tienes tareas pendientes para hoy.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}