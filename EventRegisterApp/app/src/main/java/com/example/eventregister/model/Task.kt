package com.example.eventregister.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una tarea persistida en la base de datos local SQLite mediante Room.
 *
 * @param id Identificador único autogenerado por Room.
 * @param title Título o nombre de la tarea.
 * @param description Descripción detallada de la tarea.
 * @param isCompleted Estado de completitud de la tarea.
 * @param dueDate Fecha límite de la tarea expresada en milisegundos (timestamp Long).
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDate: Long = System.currentTimeMillis()
)
