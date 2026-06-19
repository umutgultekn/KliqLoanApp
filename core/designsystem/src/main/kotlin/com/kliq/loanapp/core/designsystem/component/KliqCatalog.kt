package com.kliq.loanapp.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/**
 * A single design-system showcase, previewed in BOTH light and dark below. It is the one artifact a
 * reviewer can open to confirm the token system, every component variant, and — crucially — the dark
 * theme actually render, without installing the app. It also doubles as the natural surface for
 * future screenshot tests. Private + preview-only, so R8 strips it from release.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KliqCatalog() {
    val badges = listOf(
        "ACTIVE" to Tone.Active,
        "OVERDUE" to Tone.Overdue,
        "DEFAULT" to Tone.Default,
        "PAID" to Tone.Paid,
        "PERSONAL" to Tone.TypePersonal,
        "MORTGAGE" to Tone.TypeMortgage,
        "AUTO" to Tone.TypeAuto,
        "BUSINESS" to Tone.TypeBusiness,
    )
    Surface(color = KliqTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(KliqTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(KliqTheme.spacing.xl),
        ) {
            CatalogSection("Buttons") {
                PrimaryButton(text = "Primary", onClick = {}, fullWidth = true)
                Spacer(Modifier.height(KliqTheme.spacing.sm))
                SecondaryButton(text = "Secondary", onClick = {}, fullWidth = true)
                Spacer(Modifier.height(KliqTheme.spacing.sm))
                DestructiveButton(text = "Destructive", onClick = {}, fullWidth = true)
            }
            CatalogSection("Text roles") {
                KliqText("Heading", style = KliqTextStyle.Heading)
                KliqText("Title", style = KliqTextStyle.Title)
                KliqText("$12,500", style = KliqTextStyle.Amount)
                KliqText("Body copy sample", style = KliqTextStyle.Body)
                KliqText("Caption", style = KliqTextStyle.Caption)
            }
            CatalogSection("Badges — every tone (AA foreground)") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(KliqTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KliqTheme.spacing.sm),
                ) {
                    badges.forEach { (label, tone) ->
                        StatusBadge(BadgeConfig(UiText.of(label), tone, UiText.of(label)))
                    }
                }
            }
            CatalogSection("Filter chips") {
                Row(horizontalArrangement = Arrangement.spacedBy(KliqTheme.spacing.sm)) {
                    KliqFilterChip(ChipConfig("Selected", selected = true), onClick = {})
                    KliqFilterChip(ChipConfig("Unselected", selected = false), onClick = {})
                }
            }
            CatalogSection("Card") {
                KliqCard {
                    KliqText("Card surface", style = KliqTextStyle.Title)
                    KliqText("Elevated container built from Kliq tokens", style = KliqTextStyle.Caption)
                }
            }
            CatalogSection("Form field") {
                EmailFormField(value = "ada@kliq.com", state = FieldUiState.Valid, onValueChange = {}, onImeAction = {})
            }
        }
    }
}

@Composable
private fun CatalogSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(KliqTheme.spacing.sm)) {
        KliqText(title, style = KliqTextStyle.Caption, color = KliqTheme.colors.textSecondary)
        content()
    }
}

@Preview(name = "Catalog — Light", showBackground = true, heightDp = 1500)
@Preview(name = "Catalog — Dark", showBackground = true, heightDp = 1500, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun KliqCatalogPreview() {
    KliqTheme { KliqCatalog() }
}
