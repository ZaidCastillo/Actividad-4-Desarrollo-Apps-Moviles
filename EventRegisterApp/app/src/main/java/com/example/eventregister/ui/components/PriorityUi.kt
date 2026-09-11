package com.example.eventregister.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.eventregister.model.Priority
import com.example.eventregister.theme.PriorityHighColor
import com.example.eventregister.theme.PriorityLowColor
import com.example.eventregister.theme.PriorityMediumColor

/** Color semántico asociado a cada nivel de prioridad. */
fun Priority.color(): Color = when (this) {
    Priority.BAJA -> PriorityLowColor
    Priority.MEDIA -> PriorityMediumColor
    Priority.ALTA -> PriorityHighColor
}

/** Pequeño indicador circular de color usado en las tarjetas de evento. */
@Composable
fun PriorityDot(priority: Priority, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .background(color = priority.color(), shape = CircleShape)
    )
}
