package com.example.notesapp.data.repository

import com.example.notesapp.data.local.NoteDao
import com.example.notesapp.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para abstraer el acceso a datos y permitir pruebas unitarias.
 */
class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: Flow<List<Note>> = noteDao.getAll()

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insert(note: Note): Long = noteDao.insert(note)

    suspend fun update(note: Note) = noteDao.update(note)

    suspend fun delete(note: Note) = noteDao.delete(note)

    suspend fun deleteById(id: Long) = noteDao.deleteById(id)

    suspend fun toggleCompleted(note: Note) {
        noteDao.updateCompletionStatus(note.id, !note.isCompleted)
    }
}
