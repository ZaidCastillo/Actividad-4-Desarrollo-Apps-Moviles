package com.example.eventregister.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventregister.data.repository.TaskRepository
import com.example.eventregister.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Eventos que pueden dispararse hacia la UI (Snackbars, alertas, etc.).
 */
sealed class TaskUiEvent {
    data class ShowMessage(val message: String) : TaskUiEvent()
    data class TaskDeleted(val task: Task) : TaskUiEvent()
}

/**
 * ViewModel encargado de la gestión de tareas con persistencia en Room.
 * Expone la lista reactiva a través de StateFlow y procesa eventos asíncronos con viewModelScope.
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    /**
     * Estado observable de tareas expuesto como StateFlow reactivo.
     * Cualquier inserción, actualización o eliminación en Room actualizará automáticamente este flujo.
     */
    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvent = MutableSharedFlow<TaskUiEvent>()
    val uiEvent: SharedFlow<TaskUiEvent> = _uiEvent.asSharedFlow()

    // Estado del diálogo de creación/edición
    private val _editingTask = MutableStateFlow<Task?>(null)
    val editingTask: StateFlow<Task?> = _editingTask.asStateFlow()

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen.asStateFlow()

    fun openCreateDialog() {
        _editingTask.value = null
        _isDialogOpen.value = true
    }

    fun openEditDialog(task: Task) {
        _editingTask.value = task
        _isDialogOpen.value = true
    }

    fun closeDialog() {
        _editingTask.value = null
        _isDialogOpen.value = false
    }

    /**
     * Valida y guarda una tarea (creación o edición).
     * @return true si la validación fue exitosa, false de lo contrario.
     */
    fun saveTask(
        title: String,
        description: String,
        dueDate: Long,
        existingId: Long = 0,
        isCompleted: Boolean = false
    ): Boolean {
        if (title.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(TaskUiEvent.ShowMessage("El título no puede estar vacío"))
            }
            return false
        }

        viewModelScope.launch {
            if (existingId > 0) {
                val updated = Task(
                    id = existingId,
                    title = title.trim(),
                    description = description.trim(),
                    isCompleted = isCompleted,
                    dueDate = dueDate
                )
                repository.updateTask(updated)
                _uiEvent.emit(TaskUiEvent.ShowMessage("Tarea actualizada correctamente"))
            } else {
                val newTask = Task(
                    title = title.trim(),
                    description = description.trim(),
                    isCompleted = false,
                    dueDate = dueDate
                )
                repository.addTask(newTask)
                _uiEvent.emit(TaskUiEvent.ShowMessage("Tarea agregada exitosamente"))
            }
            closeDialog()
        }
        return true
    }

    /**
     * Alterna el estado de completitud de una tarea (disparado por el Switch).
     */
    fun toggleTaskCompleted(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(task, isCompleted)
        }
    }

    /**
     * Actualiza una tarea existente.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    /**
     * Elimina una tarea de la base de datos de forma asíncrona.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _uiEvent.emit(TaskUiEvent.TaskDeleted(task))
        }
    }

    /**
     * Restaura una tarea eliminada (acción "Deshacer" del Snackbar).
     */
    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
            _uiEvent.emit(TaskUiEvent.ShowMessage("Tarea restaurada"))
        }
    }
}
