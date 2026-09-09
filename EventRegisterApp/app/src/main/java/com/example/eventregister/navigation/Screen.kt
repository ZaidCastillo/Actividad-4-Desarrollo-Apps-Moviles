package com.example.eventregister.navigation

/**
 * Rutas de navegación de la app. Usar una sealed class evita errores de
 * tipeo con strings sueltos al navegar.
 */
sealed class Screen(val route: String) {
    data object TaskList : Screen("task_list")
    data object EventList : Screen("event_list")
    data object EventForm : Screen("event_form")
}

