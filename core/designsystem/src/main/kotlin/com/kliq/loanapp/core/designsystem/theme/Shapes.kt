package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

/** Centralized corner shapes. The only place corner radii are defined. */
@Immutable
data class KliqShapes(
    val badge: CornerBasedShape = RoundedCornerShape(6.dp),
    val field: CornerBasedShape = RoundedCornerShape(14.dp),
    val button: CornerBasedShape = RoundedCornerShape(14.dp),
    val card: CornerBasedShape = RoundedCornerShape(16.dp),
    val cardLarge: CornerBasedShape = RoundedCornerShape(20.dp),
    val pill: CornerBasedShape = RoundedCornerShape(percent = 50),
)

val KliqDefaultShapes = KliqShapes()
