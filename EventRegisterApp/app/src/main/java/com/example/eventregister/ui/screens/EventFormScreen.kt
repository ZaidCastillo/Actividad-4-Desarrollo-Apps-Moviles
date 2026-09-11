package com.example.eventregister.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.eventregister.model.Event
import com.example.eventregister.model.Priority
import com.example.eventregister.viewmodel.EventViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Formulario de registro de un nuevo evento.
 *
 * Validaciones:
 * - Título obligatorio (no vacío).
 * - Fecha obligatoria y debe ser hoy o una fecha futura.
 *
 * Las validaciones se calculan con `derivedStateOf`, que recalcula
 * automáticamente su valor cada vez que cambia alguno de los estados de
 * los que depende (title, selectedDate). Es el equivalente declarativo e
 * idiomático de "validar en tiempo real"; un `LaunchedEffect` también
 * podría usarse para disparar un efecto secundario ante cada cambio, pero
 * para derivar un valor puro a partir de otro estado, `derivedStateOf` es
 * la herramienta recomendada por Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    viewModel: EventViewModel,
    onEventSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var priority by remember { mutableStateOf(Priority.MEDIA) }
    var showDatePicker by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    val titleError by remember {
        derivedStateOf {
            if (hasAttemptedSubmit && title.isBlank()) "El título es obligatorio" else null
        }
    }
    val dateError by remember {
        derivedStateOf {
            when {
                hasAttemptedSubmit && selectedDate == null -> "Selecciona una fecha"
                selectedDate != null && selectedDate!!.isBefore(LocalDate.now()) ->
                    "La fecha debe ser hoy o una fecha futura"
                else -> null
            }
        }
    }
    val isFormValid by remember {
        derivedStateOf {
            title.isNotBlank() && selectedDate != null && !selectedDate!!.isBefore(LocalDate.now())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo evento") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                isError = titleError != null,
                supportingText = {
                    if (titleError != null) {
                        Text(titleError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = selectedDate?.format(
                    DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es", "ES"))
                ) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha") },
                isError = dateError != null,
                supportingText = {
                    if (dateError != null) {
                        Text(dateError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text("Prioridad", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.etiqueta) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    hasAttemptedSubmit = true
                    if (isFormValid) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.addEvent(
                            Event(
                                title = title.trim(),
                                date = selectedDate!!,
                                priority = priority,
                                description = description.trim()
                            )
                        )
                        onEventSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar evento")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
