package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.data.local.NoteDatabase
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.ui.screens.NotesScreen
import com.example.notesapp.ui.theme.NotesAppTheme
import com.example.notesapp.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {

    private val database by lazy { NoteDatabase.getDatabase(this) }
    private val repository by lazy { NoteRepository(database.noteDao()) }

    private val viewModel: NoteViewModel by viewModels {
        NoteViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Insertar notas de muestra iniciales en background de forma segura
        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val currentNotes = repository.allNotes.first()
                if (currentNotes.isEmpty()) {
                    repository.insert(
                        Note(
                            title = "🚀 ¡Bienvenido a Notes & Tasks!",
                            content = "• Desliza hacia la izquierda o derecha para eliminar.\n• Toca el círculo para marcar como completada.\n• Toca la nota para editar su contenido y color.",
                            date = LocalDateTime.now(),
                            colorIndex = 0,
                            isPinned = true
                        )
                    )
                    repository.insert(
                        Note(
                            title = "📸 Prueba de Gestos de Zoom",
                            content = "Esta nota incluye una imagen adjunta. Toca la imagen para abrir el visor en pantalla completa y prueba hacer zoom con pellizco (pinch-to-zoom) y paneo.",
                            date = LocalDateTime.now().minusHours(1),
                            colorIndex = 3,
                            imageUri = "https://images.unsplash.com/photo-1517842645767-c639042777db?w=800&q=80",
                            isPinned = false
                        )
                    )
                    repository.insert(
                        Note(
                            title = "✅ Tarea de Ejemplo",
                            content = "Revisar el funcionamiento de Room SQLite, ViewModel y animaciones con Compose.",
                            date = LocalDateTime.now().minusDays(1),
                            isCompleted = true,
                            colorIndex = 1,
                            isPinned = false
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setContent {
            NotesAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesScreen(viewModel = viewModel)
                }
            }
        }
    }
}
