package com.example.notesapp.ui.viewmodel

import com.example.notesapp.data.model.Note

enum class NoteFilter {
    ALL,
    PENDING,
    COMPLETED
}

data class NoteUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: NoteFilter = NoteFilter.ALL,
    val isEditorOpen: Boolean = false,
    val editingNote: Note? = null,
    val previewImageUrl: String? = null,
    val recentlyDeletedNote: Note? = null,
    val totalCount: Int = 0,
    val completedCount: Int = 0
)
