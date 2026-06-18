package com.kliq.loanapp.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/**
 * Semantic text roles. A screen says what a piece of text MEANS (a heading, an amount, a caption),
 * not how it looks — the role binds both the [TextStyle] and a sensible default color from the
 * theme tokens. Mirrors the "modular label" pattern: style/color live in one place, call sites stay
 * declarative, and a role re-tune propagates everywhere.
 */
enum class KliqTextStyle { Heading, Title, Amount, Body, Label, Caption, Badge }

/**
 * Config-driven text. Pass a [style] role; the look is resolved from [KliqTheme]. Override [color]
 * only when a context needs a non-default color (e.g. on a dark surface).
 */
@Composable
fun KliqText(
    text: String,
    modifier: Modifier = Modifier,
    style: KliqTextStyle = KliqTextStyle.Body,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.textStyle(),
        color = if (color != Color.Unspecified) color else style.defaultColor(),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
    )
}

/* ----- Specialized wrappers (the Small/Medium/LargeLabel analog) ----- */

@Composable
fun HeadingText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) =
    KliqText(text, modifier, KliqTextStyle.Heading, color)

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) =
    KliqText(text, modifier, KliqTextStyle.Title, color)

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) =
    KliqText(text, modifier, KliqTextStyle.Body, color)

@Composable
fun CaptionText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) =
    KliqText(text, modifier, KliqTextStyle.Caption, color)

@Composable
@ReadOnlyComposable
private fun KliqTextStyle.textStyle(): TextStyle = with(KliqTheme.typography) {
    when (this@textStyle) {
        KliqTextStyle.Heading -> heading
        KliqTextStyle.Title -> title
        KliqTextStyle.Amount -> amount
        KliqTextStyle.Body -> body
        KliqTextStyle.Label -> label
        KliqTextStyle.Caption -> caption
        KliqTextStyle.Badge -> badge
    }
}

@Composable
@ReadOnlyComposable
private fun KliqTextStyle.defaultColor(): Color = with(KliqTheme.colors) {
    when (this@defaultColor) {
        KliqTextStyle.Caption -> textSecondary
        else -> textPrimary
    }
}
