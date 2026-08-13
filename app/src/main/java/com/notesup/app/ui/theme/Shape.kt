package com.notesup.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NotesupShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

val SheetTop = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val DialogShape = RoundedCornerShape(28.dp)
val Stadium = RoundedCornerShape(percent = 50)
