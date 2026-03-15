package com.sahe.itera.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class DaySchedule(
    val startHour: Int = 7,  val startMin: Int = 0,
    val endHour: Int   = 8,  val endMin: Int   = 0
)

@Composable
fun DayTimeRow(
    label: String,
    schedule: DaySchedule,
    accentColor: Color,
    onChanged: (DaySchedule) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label,
            style = MaterialTheme.typography.labelMedium,
            color = accentColor,
            fontWeight = FontWeight.SemiBold)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeInputPair(
                hour = schedule.startHour,
                minute = schedule.startMin,
                label = "Inicio",
                onHourChange   = { onChanged(schedule.copy(startHour = it)) },
                onMinuteChange = { onChanged(schedule.copy(startMin = it)) },
                modifier = Modifier.weight(1f)
            )
            Text("–", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            TimeInputPair(
                hour = schedule.endHour,
                minute = schedule.endMin,
                label = "Fin",
                onHourChange   = { onChanged(schedule.copy(endHour = it)) },
                onMinuteChange = { onChanged(schedule.copy(endMin = it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TimeInputPair(
    hour: Int,
    minute: Int,
    label: String,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var hourText   by remember { mutableStateOf(hour.toString().padStart(2, '0')) }
    var minuteText by remember { mutableStateOf(minute.toString().padStart(2, '0')) }
    var hourFocused by remember { mutableStateOf(false) }
    var minFocused  by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                value = hourText,
                onValueChange = { v ->
                    val f = v.filter { it.isDigit() }.take(2)
                    hourText = f
                    f.toIntOrNull()?.coerceIn(0, 23)?.let { onHourChange(it) }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                placeholder = { Text("HH", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()) },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { state ->
                        if (state.isFocused && !hourFocused) hourText = ""
                        hourFocused = state.isFocused
                    }
            )
            Text(":", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = minuteText,
                onValueChange = { v ->
                    val f = v.filter { it.isDigit() }.take(2)
                    minuteText = f
                    f.toIntOrNull()?.coerceIn(0, 59)?.let { onMinuteChange(it) }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                placeholder = { Text("MM", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()) },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { state ->
                        if (state.isFocused && !minFocused) minuteText = ""
                        minFocused = state.isFocused
                    }
            )
        }
    }
}