package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.kliq.loanapp.core.common.ui.Tone

/**
 * Luminance crossover where dark ink overtakes white for foreground contrast on a filled background
 * (derived from the WCAG contrast formula for near-black vs white text). Fills brighter than this get
 * [KliqColorScheme.badgeTextDark]; darker fills get [KliqColorScheme.onPrimary].
 */
private const val BADGE_FILL_LUMINANCE_THRESHOLD = 0.2f

/**
 * The single source of truth for app colors. Screens and ViewModels never reference raw hex —
 * they go through [KliqTheme]. This is the only place `Color(0xFF…)` literals are allowed.
 */
@Immutable
data class KliqColorScheme(
    val primary: Color,
    val onPrimary: Color,
    // De-emphasized foreground on the primary surface (e.g. the summary card's label/caption).
    val onPrimaryMuted: Color,
    // Theme-invariant dark ink for badge foregrounds; [onColorFor] picks it over white by luminance.
    val badgeTextDark: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val statusActive: Color,
    val statusOverdue: Color,
    val statusDefault: Color,
    val statusPaid: Color,
    // Darker, AA-contrast variants for status-colored TEXT on a light surface (the saturated
    // status fills above are for badge backgrounds, not foreground text).
    val statusActiveText: Color,
    val statusOverdueText: Color,
    val statusDefaultText: Color,
    val typePersonal: Color,
    val typeMortgage: Color,
    val typeAuto: Color,
    val typeBusiness: Color,
) {
    /** Resolves a semantic [Tone] to a concrete fill color (badges, accents). */
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

    /** Resolves a semantic [Tone] to an AA-contrast color for status TEXT on a light surface. */
    fun textColorFor(tone: Tone): Color = when (tone) {
        Tone.Active -> statusActiveText
        Tone.Overdue -> statusOverdueText
        Tone.Default -> statusDefaultText
        Tone.Paid -> statusPaid
        else -> textPrimary
    }

    /**
     * AA-contrast foreground for a badge filled with [colorFor]. Chooses dark ink or [onPrimary] by
     * the fill's luminance, so it stays legible across both themes (the same [Tone] resolves to a
     * lighter fill in dark mode). Replaces the previous always-white badge text, which fell below
     * WCAG AA on most fills.
     */
    fun onColorFor(tone: Tone): Color =
        if (colorFor(tone).luminance() > BADGE_FILL_LUMINANCE_THRESHOLD) badgeTextDark else onPrimary
}

val KliqLightColors = KliqColorScheme(
    primary = Color(0xFF222B45),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryMuted = Color(0xB3FFFFFF), // white @ 70% — de-emphasized text on the primary surface
    badgeTextDark = Color(0xFF1A1A2E),
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF8C8C94),
    border = Color(0xFFD8D8DE),
    statusActive = Color(0xFF2EB873),
    statusOverdue = Color(0xFFF29E26),
    // Darkened from #E63835 so white badge text clears WCAG AA (4.5:1): #E63835 sat at 4.22:1.
    statusDefault = Color(0xFFC62828),
    statusPaid = Color(0xFF8C8C94),
    statusActiveText = Color(0xFF1B7A4B),
    statusOverdueText = Color(0xFFA66200),
    statusDefaultText = Color(0xFFC62828),
    typePersonal = Color(0xFF212B45),
    typeMortgage = Color(0xFF29809A),
    typeAuto = Color(0xFF33998F),
    typeBusiness = Color(0xFF945724),
)

val KliqDarkColors = KliqColorScheme(
    primary = Color(0xFF3D5A99),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryMuted = Color(0xB3FFFFFF), // white @ 70% — de-emphasized text on the primary surface
    badgeTextDark = Color(0xFF1A1A2E),
    background = Color(0xFF121218),
    surface = Color(0xFF1E1E28),
    textPrimary = Color(0xFFECECF1),
    textSecondary = Color(0xFF9A9AA5),
    border = Color(0xFF3A3A46),
    statusActive = Color(0xFF3DD68C),
    statusOverdue = Color(0xFFFFB23E),
    statusDefault = Color(0xFFFF6B66),
    statusPaid = Color(0xFF8C8C94),
    statusActiveText = Color(0xFF4FD992),
    statusOverdueText = Color(0xFFFFC061),
    statusDefaultText = Color(0xFFFF7B76),
    typePersonal = Color(0xFF8FA8DC),
    typeMortgage = Color(0xFF5FB8D6),
    typeAuto = Color(0xFF5FC7BC),
    typeBusiness = Color(0xFFC79A6B),
)
