# Notes & Tasks Management App (Android Jetpack Compose + Room)

Aplicación nativa de gestión de notas desarrollada en **Kotlin** con **Jetpack Compose**, **Room SQLite**, **Animaciones Avanzadas**, **Gestos Táctiles** e **Interfaz Adaptable**.

---

## 📱 Características Implementadas y Cumplimiento de Requerimientos

### 1. Animaciones Fluidas y Feedback Visual
- **Transiciones al agregar y eliminar notas**: Uso de `AnimatedVisibility` con efectos combinados de `slideInHorizontally() + fadeIn()` para entrada y `slideOutVertically() + fadeOut()` al eliminar.
- **Feedback visual al completar tareas**: Animación de rebote (*spring bounce*) con `animateFloatAsState` en el checkmark, transición de opacidad y tachado de texto animado (*line-through*).
- **Transiciones de color**: Uso de `animateColorAsState` para cambio dinámico de bordes y fondo de tarjetas según su estado y categoría.

### 2. Gestos Táctiles
- **Deslizar para eliminar (Swipe-to-Dismiss)**: Implementado mediante `SwipeToDismissBox` con fondo reactivo (ícono de papelera y color rojo de advertencia) y opción de **Deshacer (Undo)** mediante Snackbar interactivo.
- **Zoom con pellizco (Pinch-to-Zoom)**: Visualizador de imágenes adjuntas en pantalla completa con soporte de:
  - Zoom multitáctil con pellizco (`detectTransformGestures`) en un rango de 0.8x a 5.0x.
  - Paneo / arrastre con los dedos.
  - Doble toque táctil (`detectTapGestures`) para ampliar o reiniciar el zoom.
  - Indicador flotante en tiempo real del porcentaje de zoom actual.

### 3. Base de Datos Local (Room + SQLite) con CRUD
- **Entidad `Note`**:
  ```kotlin
  @Entity(tableName = "notes")
  data class Note(
      @PrimaryKey(autoGenerate = true) val id: Long = 0,
      val title: String,
      val content: String,
      val date: LocalDateTime = LocalDateTime.now(),
      val isCompleted: Boolean = false,
      val imageUri: String? = null,
      val colorIndex: Int = 0,
      val isPinned: Boolean = false
  )
  ```
- **TypeConverters**: Serialización nativa de `LocalDateTime` (ISO-8601).
- **Operaciones CRUD**: Inserción, consulta con `Flow`, actualización, eliminación, fijado de notas y búsqueda en tiempo real.
- **Patrón Repository**: Separación limpia entre persistencia y capa de presentación.

### 4. Arquitectura y Manejo de Estado
- **`ViewModel` + Kotlin Coroutines + StateFlow**: Manejo reactivo de estado con `uiState` (`SharingStarted.WhileSubscribed`).
- Búsqueda en tiempo real (`flatMapLatest`) y filtros interactivos (*Todas*, *Pendientes*, *Completadas*).

### 5. Interfaz Adaptable y Responsiva
- Soporte para smartphones, pantallas plegables y tablets usando `BoxWithConstraints`:
  - **Pantallas pequeñas (<600dp)**: 1 columna vertical.
  - **Pantallas medianas (600dp - 900dp)**: Cuadrícula escalonada de 2 columnas (`LazyVerticalStaggeredGrid`).
  - **Pantallas grandes / Tablets (≥900dp o modo horizontal)**: Cuadrícula escalonada de 3 columnas.

### 6. Accesibilidad y Diseño
- Paleta Material 3 accesible con ratios de contraste ≥ 4.5:1.
- Soporte automático para **Modo Claro** y **Modo Oscuro** del sistema con colores optimizados.

---

## 🚀 Cómo Abrir y Ejecutar en Android Studio

1. Abre **Android Studio** (Hedgehog, Iguana, Jellyfish, Koala o superior).
2. Selecciona **Open** y navega a esta carpeta:
   ```
   c:\Users\2a8ca\Desktop\Actividad-3-Desarrollo-Apps-Moviles\Act 4
   ```
3. Espera a que Gradle sincronice las dependencias automáticamente.
4. Selecciona un emulador (por ejemplo *Pixel 8 / Pixel Fold / Tablet*) o conecta tu dispositivo físico con Depuración USB habilitada.
5. Haz clic en **Run 'app'** (`Shift + F10` o el botón verde ▶).

---

## 🧪 Pruebas de Funcionamiento

- **Probar Swipe-to-Delete**: Desliza cualquier tarjeta hacia la izquierda o derecha para ver la animación de eliminación y el mensaje de "Deshacer".
- **Probar Pinch-to-Zoom**: Toca la nota con imagen ("📸 Prueba de Gestos de Zoom") para abrir el visor y pellizca con dos dedos para hacer zoom y desplazarte.
- **Probar Feedback de Tareas**: Toca el ícono del círculo en cualquier nota para ver el rebote y el tachado animado.
- **Probar Filtros y Búsqueda**: Usa la barra superior o los chips de *Pendientes* / *Completadas*.
- **Probar Persistencia**: Cierra la app y vuelve a abrirla; todas las notas creadas, editadas o completadas se mantendrán intactas en SQLite.
