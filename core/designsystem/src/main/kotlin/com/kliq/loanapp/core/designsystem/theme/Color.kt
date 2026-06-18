package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.kliq.loanapp.core.common.ui.Tone

/**
 * The single source of truth for app colors. Screens and ViewModels never reference raw hex —
 * they go through [KliqTheme]. This is the only place `Color(0xFF…)` literals are allowed.
 */
@Immutable
data class KliqColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val statusActive: Color,
    val statusOverdue: Color,
    val statusDefault: Color,
    val statusPaid: Color,
    val typePersonal: Color,
    val typeMortgage: Color,
    val typeAuto: Color,
    val typeBusiness: Color,
) {
    /** Resolves a semantic [Tone] to a concrete color. */
    fun colorFor(tone: Tone): Color = when (tone) {
        Tone.Neutral -> textSecondary
        Tone.Primary -> primary
        Tone.Active -> statusActive
        Tone.Overdue -> statusOverdue
        Tone.Default -> statusDefault
        Tone.Paid -> statusPaid
        Tone.TypePersonal -> typePersonal
        Tone.TypeMortgage -> typeMortgage
        Tone.TypeAuto -> typeAuto
        Tone.TypeBusiness -> typeBusiness
    }
}

val KliqLightColors = KliqColorScheme(
    primary = Color(0xFF222B45),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF8C8C94),
    border = Color(0xFFD8D8DE),
    statusActive = Color(0xFF2EB873),
    statusOverdue = Color(0xFFF29E26),
    statusDefault = Color(0xFFE63835),
    statusPaid = Color(0xFF8C8C94),
    typePersonal = Color(0xFF212B45),
    typeMortgage = Color(0xFF29809A),
    typeAuto = Color(0xFF33998F),
    typeBusiness = Color(0xFF945724),
)
