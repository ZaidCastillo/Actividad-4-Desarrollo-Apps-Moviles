package com.example.eventregister.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eventregister.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad Task.
 * Proporciona métodos para operaciones CRUD sobre la tabla 'tasks'.
 * Utiliza Flow de Coroutines para garantizar reactividad en tiempo real con Jetpack Compose.
 */
@Dao
interface TaskDao {

    /**
     * Inserta una nueva tarea o reemplaza una existente si hay conflicto de clave primaria.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    /**
     * Actualiza los datos de una tarea existente.
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Elimina una tarea de la base de datos.
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Obtiene la lista observable de todas las tareas ordenadas por estado (pendientes primero)
     * y por fecha límite ascendente.
     */
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Obtiene una tarea específica por su ID.
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Long): Flow<Task?>
}
