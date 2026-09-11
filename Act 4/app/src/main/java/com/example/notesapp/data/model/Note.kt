package com.example.notesapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidad Note para Room SQLite.
 * Incluye soporte para título, contenido, fecha (LocalDateTime),
 * estado de completado, imagen adjunta y color de acento.
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val isCompleted: Boolean = false,
    val imageUri: String? = null,
    val colorIndex: Int = 0,
    val isPinned: Boolean = false
)
