package com.example.notesapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.notesapp.data.model.Note
import com.example.notesapp.ui.theme.NoteColors
import com.example.notesapp.ui.theme.NoteDarkBackgrounds
import com.example.notesapp.ui.theme.NoteLightBackgrounds
import java.time.format.DateTimeFormatter

/**
 * Tarjeta de Nota interactiva con:
 * - Gesto Swipe-to-Dismiss (deslizar para eliminar con feedback visual)
 * - Animación de completado / tachado reactivo
 * - Miniatura con indicador de zoom táctil
 * - Paleta accesible con alto contraste
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnDelete by rememberUpdatedState(onDelete)
    val isDark = isSystemInDarkTheme()

    // Estado del gesto deslizar para eliminar
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                currentOnDelete()
                true
            } else {
                false
            }
        }
    )

    // Animaciones de feedback visual al completar
    val checkmarkScale by animateFloatAsState(
        targetValue = if (note.isCompleted) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkmarkScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (note.isCompleted) 0.55f else 1f,
        label = "contentAlpha"
    )

    // Colores de acento según la nota
    val safeColorIndex = note.colorIndex.coerceIn(0, NoteColors.lastIndex)
    val accentColor = NoteColors[safeColorIndex]
    val cardBgColor by animateColorAsState(
        targetValue = if (isDark) {
            if (note.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            else NoteDarkBackgrounds[safeColorIndex]
        } else {
            if (note.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else NoteLightBackgrounds[safeColorIndex]
        },
        label = "cardBgColor"
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.clip(RoundedCornerShape(18.dp)),
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.EndToStart, SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.error
                else -> Color.Transparent
            }
            val alignment = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Alignment.CenterStart
            } else {
                Alignment.CenterEnd
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar nota",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        content = {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (note.isPinned) accentColor else accentColor.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (note.isPinned) 4.dp else 1.5.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Cabecera de la nota: Pin + Título + Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (note.isPinned) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Nota fijada",
                                    tint = accentColor,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = note.title.ifBlank { "Sin título" },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Botón Checkbox interactivo con animación
                        IconButton(
                            onClick = onToggleComplete,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(checkmarkScale)
                        ) {
                            Icon(
                                imageVector = if (note.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = if (note.isCompleted) "Marcar pendiente" else "Completar tarea",
                                tint = if (note.isCompleted) accentColor else MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Contenido de la nota
                    if (note.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Imagen adjunta con indicador de zoom
                    if (!note.imageUri.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onImageClick(note.imageUri) }
                        ) {
                            AsyncImage(
                                model = note.imageUri,
                                contentDescription = "Imagen adjunta a la nota",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Insignia "Toca para Zoom"
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.65f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomIn,
                                        contentDescription = "Pellizca para zoom",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Pellizca para zoom",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Pie de la tarjeta: Fecha formateada y etiqueta de color
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.date.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        // Indicador de color de categoría
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(accentColor, CircleShape)
                        )
                    }
                }
            }
        }
    )
}
