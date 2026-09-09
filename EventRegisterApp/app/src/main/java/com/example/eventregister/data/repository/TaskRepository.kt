package com.example.eventregister.data.repository

import com.example.eventregister.data.local.TaskDao
import com.example.eventregister.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio que centraliza el acceso a los datos de tareas.
 * Se comunica directamente con el DAO para realizar operaciones de persistencia.
 */
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    /**
     * Flujo reactivo con la lista completa de tareas.
     */
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    /**
     * Inserta una nueva tarea.
     */
    suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    /**
     * Actualiza una tarea existente.
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    /**
     * Elimina una tarea.
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    /**
     * Cambia el estado de completado de una tarea.
     */
    suspend fun setTaskCompleted(task: Task, isCompleted: Boolean) {
        taskDao.updateTask(task.copy(isCompleted = isCompleted))
    }

    /**
     * Obtiene una tarea por ID.
     */
    fun getTaskById(id: Long): Flow<Task?> {
        return taskDao.getTaskById(id)
    }
}
