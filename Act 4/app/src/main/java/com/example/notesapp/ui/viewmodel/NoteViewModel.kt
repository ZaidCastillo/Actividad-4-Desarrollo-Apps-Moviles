package com.example.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(NoteFilter.ALL)
    private val _isEditorOpen = MutableStateFlow(false)
    private val _editingNote = MutableStateFlow<Note?>(null)
    private val _previewImageUrl = MutableStateFlow<String?>(null)
    private val _recentlyDeletedNote = MutableStateFlow<Note?>(null)

    // Flow reactivo de notas dependiendo de la búsqueda
    private val _notesFlow = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.allNotes
        } else {
            repository.searchNotes(query)
        }
    }

    private val _notesAndFilter = combine(_notesFlow, _searchQuery, _selectedFilter) { notes, query, filter ->
        Triple(notes, query, filter)
    }

    // Estado unificado expuesto como StateFlow para la UI de Compose
    val uiState: StateFlow<NoteUiState> = combine(
        _notesAndFilter,
        _isEditorOpen,
        _editingNote,
        _previewImageUrl,
        _recentlyDeletedNote
    ) { (notes, query, filter), isEditorOpen, editingNote, previewImageUrl, recentlyDeleted ->
        val filteredList = when (filter) {
            NoteFilter.ALL -> notes
            NoteFilter.PENDING -> notes.filter { !it.isCompleted }
            NoteFilter.COMPLETED -> notes.filter { it.isCompleted }
        }

        NoteUiState(
            notes = filteredList,
            searchQuery = query,
            selectedFilter = filter,
            isEditorOpen = isEditorOpen,
            editingNote = editingNote,
            previewImageUrl = previewImageUrl,
            recentlyDeletedNote = recentlyDeleted,
            totalCount = notes.size,
            completedCount = notes.count { it.isCompleted }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoteUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: NoteFilter) {
        _selectedFilter.value = filter
    }

    fun openEditor(note: Note? = null) {
        _editingNote.value = note
        _isEditorOpen.value = true
    }

    fun closeEditor() {
        _isEditorOpen.value = false
        _editingNote.value = null
    }

    fun openImagePreview(uri: String) {
        _previewImageUrl.value = uri
    }

    fun closeImagePreview() {
        _previewImageUrl.value = null
    }

    fun saveNote(
        title: String,
        content: String,
        imageUri: String? = null,
        colorIndex: Int = 0,
        isPinned: Boolean = false
    ) {
        viewModelScope.launch {
            val currentNote = _editingNote.value
            if (currentNote != null) {
                // Actualizar nota existente
                val updated = currentNote.copy(
                    title = title.trim(),
                    content = content.trim(),
                    imageUri = imageUri,
                    colorIndex = colorIndex,
                    isPinned = isPinned,
                    date = LocalDateTime.now()
                )
                repository.update(updated)
            } else {
                // Crear nueva nota
                val newNote = Note(
                    title = title.trim(),
                    content = content.trim(),
                    imageUri = imageUri,
                    colorIndex = colorIndex,
                    isPinned = isPinned,
                    date = LocalDateTime.now()
                )
                repository.insert(newNote)
            }
            closeEditor()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            _recentlyDeletedNote.value = note
            repository.delete(note)
        }
    }

    fun undoDelete() {
        val noteToRestore = _recentlyDeletedNote.value ?: return
        viewModelScope.launch {
            repository.insert(noteToRestore)
            _recentlyDeletedNote.value = null
        }
    }

    fun clearRecentlyDeleted() {
        _recentlyDeletedNote.value = null
    }

    fun toggleComplete(note: Note) {
        viewModelScope.launch {
            repository.toggleCompleted(note)
        }
    }

    class Factory(private val repository: NoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                return NoteViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
