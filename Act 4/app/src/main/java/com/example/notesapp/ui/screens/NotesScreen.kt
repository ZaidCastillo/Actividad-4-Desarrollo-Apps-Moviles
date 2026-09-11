package com.example.notesapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notesapp.ui.components.EmptyNotesPlaceholder
import com.example.notesapp.ui.components.ImagePinchZoomDialog
import com.example.notesapp.ui.components.NoteCard
import com.example.notesapp.ui.components.NoteEditDialog
import com.example.notesapp.ui.viewmodel.NoteFilter
import com.example.notesapp.ui.viewmodel.NoteViewModel

/**
 * Pantalla principal con diseño responsivo (adaptable para celulares y tablets):
 * - Dispositivos pequeños (<600dp): 1 columna.
 * - Dispositivos medianos (600dp - 900dp): 2 columnas.
 * - Dispositivos grandes (>=900dp / tablets): 3 o 4 columnas.
 * - Animaciones de AnimatedVisibility para agregar/eliminar notas.
 * - Búsqueda en tiempo real y filtros interactivos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejo de notificación Snackbar con acción Deshacer (Undo)
    LaunchedEffect(uiState.recentlyDeletedNote) {
        val deleted = uiState.recentlyDeletedNote
        if (deleted != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Nota \"${deleted.title.ifBlank { "Sin título" }}\" eliminada",
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearRecentlyDeleted()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Notas y Tareas",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${uiState.completedCount} de ${uiState.totalCount} completadas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openEditor() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar Nota") },
                text = { Text("Nueva Nota") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Diseño Adaptable: calcular número de columnas según el ancho de pantalla
            val columns = when {
                maxWidth >= 900.dp -> 3
                maxWidth >= 600.dp -> 2
                else -> 1
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Barra de Búsqueda
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Buscar en notas...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar búsqueda"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Chips de Filtros Animados
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedFilter == NoteFilter.ALL,
                        onClick = { viewModel.onFilterChange(NoteFilter.ALL) },
                        label = { Text("Todas (${uiState.totalCount})") },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    FilterChip(
                        selected = uiState.selectedFilter == NoteFilter.PENDING,
                        onClick = { viewModel.onFilterChange(NoteFilter.PENDING) },
                        label = { Text("Pendientes (${uiState.totalCount - uiState.completedCount})") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = uiState.selectedFilter == NoteFilter.COMPLETED,
                        onClick = { viewModel.onFilterChange(NoteFilter.COMPLETED) },
                        label = { Text("Completadas (${uiState.completedCount})") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Contenido Principal: Lista/Cuadrícula de Notas o Estado Vacío
                if (uiState.notes.isEmpty()) {
                    EmptyNotesPlaceholder(
                        isSearching = uiState.searchQuery.isNotBlank(),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(columns),
                        contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.notes,
                            key = { it.id }
                        ) { note ->
                            NoteCard(
                                note = note,
                                onClick = { viewModel.openEditor(note) },
                                onToggleComplete = { viewModel.toggleComplete(note) },
                                onDelete = { viewModel.deleteNote(note) },
                                onImageClick = { uri -> viewModel.openImagePreview(uri) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal de Creación / Edición de Nota
    if (uiState.isEditorOpen) {
        NoteEditDialog(
            initialNote = uiState.editingNote,
            onDismiss = { viewModel.closeEditor() },
            onSave = { title, content, imageUri, colorIndex, isPinned ->
                viewModel.saveNote(title, content, imageUri, colorIndex, isPinned)
            }
        )
    }

    // Modal de Previsualización de Imagen con Zoom por Pellizco (Pinch to zoom)
    val previewUri = uiState.previewImageUrl
    if (previewUri != null) {
        ImagePinchZoomDialog(
            imageUri = previewUri,
            onDismiss = { viewModel.closeImagePreview() }
        )
    }
}
