package com.example.eventregister.model

import java.time.LocalDate
import java.util.UUID

/**
 * Nivel de prioridad de un evento/tarea.
 * La etiqueta es lo que se muestra en la UI (español).
 */
enum class Priority(val etiqueta: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta")
}

/**
 * Representa un evento/recordatorio registrado por el usuario.
 *
 * @param id identificador único, generado automáticamente.
 * @param title título corto del evento (obligatorio).
 * @param date fecha del evento (debe ser hoy o futura, validado en el formulario).
 * @param priority nivel de prioridad.
 * @param description texto libre con detalles adicionales (opcional).
 */
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: LocalDate,
    val priority: Priority,
    val description: String
)
