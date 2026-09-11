package com.example.eventregister.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventregister.ui.screens.EventFormScreen
import com.example.eventregister.ui.screens.EventListScreen
import com.example.eventregister.ui.screens.TaskScreen
import com.example.eventregister.viewmodel.EventViewModel

/**
 * Grafo de navegación de la app.
 *
 * Incluye la pantalla de gestión persistente de Tareas con Room (TaskScreen)
 * como destino inicial, permitiendo también acceder a la sección de eventos.
 */
@Composable
fun EventNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val eventViewModel: EventViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskScreen(
                onNavigateToEvents = { navController.navigate(Screen.EventList.route) }
            )
        }
        composable(Screen.EventList.route) {
            EventListScreen(
                viewModel = eventViewModel,
                onAddEvent = { navController.navigate(Screen.EventForm.route) },
                onNavigateToTasks = { navController.navigate(Screen.TaskList.route) }
            )
        }
        composable(Screen.EventForm.route) {
            EventFormScreen(
                viewModel = eventViewModel,
                onEventSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
