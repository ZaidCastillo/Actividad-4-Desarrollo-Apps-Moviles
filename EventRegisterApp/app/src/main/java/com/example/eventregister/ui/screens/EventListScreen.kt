package com.example.eventregister.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventregister.ui.components.EmptyState
import com.example.eventregister.ui.components.EventCard
import com.example.eventregister.ui.components.SwipeToDeleteContainer
import com.example.eventregister.viewmodel.EventViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal: lista de eventos con tarjetas plegables.
 *
 * - `LazyColumn` + `Modifier.animateItem()` anima automáticamente la
 *   inserción, eliminación y reordenamiento de tarjetas (sin animaciones
 *   manuales de offset).
 * - Cada fila está envuelta en `SwipeToDeleteContainer` para el gesto de
 *   deslizar y eliminar.
 * - Al eliminar se muestra un Snackbar con opción de "Deshacer".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    onAddEvent: () -> Unit,
    onNavigateToTasks: (() -> Unit)? = null
) {
    val events = viewModel.events
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis eventos") },
                actions = {
                    if (onNavigateToTasks != null) {
                        IconButton(onClick = onNavigateToTasks) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = "Ver tareas Room"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar evento")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (events.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    SwipeToDeleteContainer(
                        event = event,
                        onDelete = { deleted ->
                            viewModel.removeEvent(deleted)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "«${deleted.title}» eliminado",
                                    actionLabel = "Deshacer"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.addEvent(deleted)
                                }
                            }
                        },
                        modifier = Modifier.animateItem(placementSpec = tween(300))
                    ) {
                        EventCard(event = event)
                    }
                }
            }
        }
    }
}
