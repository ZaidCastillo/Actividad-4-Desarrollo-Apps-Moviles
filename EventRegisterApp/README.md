# 📱 Documentación de la Aplicación: EventRegisterApp

## 🎯 ¿Qué hace esta aplicación?
**EventRegisterApp** es una aplicación móvil diseñada para ayudar a los usuarios a registrar, organizar y gestionar sus eventos o tareas pendientes de forma sencilla, intuitiva e interactiva. 

La aplicación permite visualizar una lista de eventos guardados, consultar sus detalles al desplegarlos, agregar nuevos eventos a través de un formulario con validaciones en tiempo real y eliminar eventos fácilmente mediante gestos táctiles.

---

## 1. ⚙️ Configuración Inicial
La aplicación fue creada en **Android Studio** utilizando **Jetpack Compose** para construir una interfaz de usuario moderna, reactiva y fluida. La estructura del proyecto está organizada en los siguientes paquetes principales para mantener el código ordenado y mantenible:

- **`ui`**: Contiene todo el diseño visual, incluyendo pantallas y componentes reutilizables.
- **`model`**: Define la estructura de información de un evento (título, fecha, prioridad y descripción).
- **`navigation`**: Administra el flujo y los cambios entre las diferentes pantallas de la app.
- **`theme`**: Guarda la paleta de colores, tipografías y estilos visuales basados en Material Design 3.

### Gestión de Estado (`EventViewModel`)
La app utiliza la clase `EventViewModel` para gestionar el estado de los datos. Su función principal es almacenar la lista de eventos en memoria. Cuando el usuario agrega o elimina un evento, el ViewModel actualiza la lista automáticamente y la interfaz de usuario se recompone al instante para reflejar los cambios.

---

## 2. 🎴 Diseño de Componentes: Tarjeta Plegable (`EventCard`)
Cada evento se presenta dentro de una **tarjeta interactiva y plegable**:

- **Modo Contraído**: Muestra únicamente la información esencial (título, fecha e indicador de prioridad) para optimizar el espacio en pantalla.
- **Modo Expandido**: Al tocar la tarjeta o el icono de flecha, la tarjeta adapta su tamaño suavemente (`animateContentSize`) y se despliega para mostrar la descripción completa y la etiqueta de prioridad. Al volver a tocarla, se contrae.

---

## 3. 🗺️ Navegación y Estados
La aplicación cuenta con dos pantallas principales administradas mediante un sistema de navegación (`NavHost`):

1. **Pantalla de Lista de Eventos**: Muestra la lista de todos los eventos registrados.
2. **Pantalla de Formulario**: Permite al usuario capturar y registrar un nuevo evento.

Mediante el uso de **Hilt** (`hiltViewModel`), ambas pantallas comparten la misma instancia de `EventViewModel`. Gracias a esto, al guardar un nuevo evento en el formulario, este aparece inmediatamente al regresar a la pantalla principal.

---

## 4. ✨ Animaciones y Gestos

### 👈 Deslizamiento para Eliminar (`swipeToDismiss`)
Para eliminar un evento de la lista no es necesario presionar botones adicionales. Basta con **deslizar la tarjeta hacia la izquierda o hacia la derecha** (`swipeToDismiss`). Durante el gesto, el fondo cambia progresivamente a color rojo indicando la acción de borrado y se activa una confirmación háptica (vibración). Además, se muestra una notificación flotante (*Snackbar*) con la opción de deshacer la acción.

### 🎬 Animación al Agregar Eventos
Al crear un evento, este no aparece de forma brusca. La aplicación incluye una **animaciones de movimiento y posición** (`animateDpAsState` / `animateItem`), logrando que la nueva tarjeta se deslice y se acomode suavemente en la lista.

---

## 5. 📝 Formulario con Validaciones
Para registrar un evento se utiliza la pantalla de formulario, la cual cuenta con **validaciones en tiempo real** (`LaunchedEffect` / `derivedStateOf`):

- **Campo de Título Obligatorio**: Si el usuario intenta guardar un evento sin haber ingresado un título, la app detecta el campo vacío y muestra un mensaje de advertencia en color rojo:  
  `¡Título requerido!`
- **Control de Errores**: Los campos y botones evitan el registro de eventos incompletos o con fechas pasadas inválidas.

---

## 6. 🧪 Pruebas y Ajustes

### 📱 Pruebas en Múltiples Dispositivos y Tamaños de Pantalla
Se evaluó la aplicación en dispositivos con pantallas pequeñas y grandes, así como en orientación horizontal (*landscape*), garantizando que:
- La lista de eventos se adapte a cualquier tamaño de pantalla sin empalmar la información.
- El formulario se pueda desplazar verticalmente cuando el teclado en pantalla esté abierto.

### ♿ Accesibilidad con TalkBack
Se realizaron ajustes de accesibilidad para garantizar que la aplicación pueda ser utilizada por personas con discapacidad visual mediante el lector de pantalla **TalkBack**:
- Todos los elementos interactivos e iconos cuentan con etiquetas semánticas (`contentDescription`) que describen su función (por ejemplo: *"Expandir detalles"*, *"Elegir fecha"*, *"Eliminar evento"*).
- Las tarjetas e iconos cuentan con áreas de toque con dimensiones óptimas para facilitar la interacción táctil.

---

## 7. 🗄️ Persistencia de Datos Local con Room (Actividad 3)

Se integró una arquitectura completa de persistencia de datos local en SQLite mediante **Room Database**, logrando un flujo reactivo unidireccional de datos con **Jetpack Compose**:

### 📦 1. Modelado de Datos (`Task`)
- **Entidad Room (`@Entity(tableName = "tasks")`)**:
  - `id: Long`: Clave primaria autogenerada (`@PrimaryKey(autoGenerate = true)`).
  - `title: String`: Título obligatorio de la tarea.
  - `description: String`: Descripción detallada opcional.
  - `isCompleted: Boolean`: Estado booleano de la tarea.
  - `dueDate: Long`: Fecha límite en formato timestamp (milisegundos).

### ⚡ 2. Acceso a Datos (`TaskDao`)
- Métodos CRUD asíncronos:
  - `@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertTask(task: Task): Long`
  - `@Update suspend fun updateTask(task: Task)`
  - `@Delete suspend fun deleteTask(task: Task)`
  - `@Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC") fun getAllTasks(): Flow<List<Task>>` (Uso de **`Flow`** para reactividad automática).

### 🏛️ 3. Base de Datos y Repositorio
- **`AppDatabase`**: Clase abstracta heredando de `RoomDatabase`, implementada con el patrón **Singleton** (`@Volatile` y `synchronized(this)`).
- **`TaskRepository`**: Abstrae las operaciones de persistencia del DAO para que la capa de presentación consuma una API limpia y desacoplada.
- **`DatabaseModule` (Hilt)**: Inyección de dependencias para proveer instancias `@Singleton` en la aplicación.

### 🧠 4. Lógica de Presentación (`TaskViewModel`)
- Expone la lista reactiva mediante **`StateFlow<List<Task>>`** (`stateIn(viewModelScope, ...)`).
- Operaciones asíncronas seguras con **`viewModelScope.launch`** para `saveTask`, `deleteTask`, `updateTask` y `toggleTaskCompleted`.
- **Validaciones de entrada**: Comprueba que el campo `title` no esté en blanco y emite eventos informativos a la UI.

### 🎨 5. Interfaz Reactiva en Jetpack Compose (`TaskScreen`)
- **`LazyColumn`**: Renderizado eficiente y reactivo de tareas con animaciones de estado.
- **`FloatingActionButton` (FAB)**: Botón de acción para abrir el diálogo de creación.
- **`Switch` interactivo**: Permite marcar o desmarcar cada tarea como completada, persistiendo el cambio al instante en Room y actualizando la UI de forma reactiva.
- **`TaskDialog`**: Diálogo modal con selección de fechas (`DatePickerDialog`), validación en tiempo real y soporte para creación y edición de tareas.
- **Métricas y Filtros**: Barra superior con conteo de tareas (Total, Pendientes, Completadas) y chips de filtrado interactivo.

