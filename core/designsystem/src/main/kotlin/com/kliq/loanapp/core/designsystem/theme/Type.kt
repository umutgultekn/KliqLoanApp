package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The single source of truth for typography. Screens never hardcode `.sp` sizes — they use
 * [KliqTheme.typography]. This is the only place text-size literals are allowed.
 */
@Immutable
data class KliqTypography(
    val heading: TextStyle,
    val title: TextStyle,
    val amount: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
    val badge: TextStyle,
)

val KliqDefaultTypography = KliqTypography(
    heading = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    title = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    amount = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    body = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
    label = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    caption = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    badge = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold),
)
