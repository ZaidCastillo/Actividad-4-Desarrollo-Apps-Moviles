package com.example.eventregister.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.eventregister.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel central de la app. Se inyecta con Hilt y se comparte entre
 * EventListScreen y EventFormScreen a través del NavGraph, de modo que
 * ambas pantallas observan la misma lista de eventos.
 *
 * `mutableStateListOf` expone una lista observable por Compose: cualquier
 * add/remove dispara recomposición automáticamente en quien la lea.
 */
@HiltViewModel
class EventViewModel @Inject constructor() : ViewModel() {

    val events = mutableStateListOf<Event>()

    fun addEvent(event: Event) {
        events.add(0, event)
    }

    fun removeEvent(event: Event) {
        events.remove(event)
    }
}
