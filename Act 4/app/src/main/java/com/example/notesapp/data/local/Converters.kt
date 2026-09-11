package com.example.notesapp.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Convertidor de tipos para que Room almacene y recupere LocalDateTime de forma nativa.
 */
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            try {
                LocalDateTime.parse(it, formatter)
            } catch (e: Exception) {
                LocalDateTime.now()
            }
        }
    }
}
