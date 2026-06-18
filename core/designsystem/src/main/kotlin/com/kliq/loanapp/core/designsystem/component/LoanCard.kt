package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/**
 * Immutable, presentation-ready config for a loan row. Holds already-formatted strings (produced by
 * the presentation mapper using providers) and semantic tones — no domain types, no raw colors.
 */
@Immutable
data class LoanCardConfig(
    val id: String,
    val title: String,
    val amountText: String,
    val rateText: String,
    val dueText: String,
    val dueTone: Tone,
    val typeBadge: BadgeConfig,
    val statusBadge: BadgeConfig,
)

@Composable
fun LoanCard(
    config: LoanCardConfig,
    modifier: Modifier = Modifier,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    val colors = KliqTheme.colors
    KliqCard(modifier = modifier.semantics(mergeDescendants = true) {}) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = config.title,
                style = KliqTheme.typography.title,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            StatusBadge(config.typeBadge)
            Spacer(Modifier.width(KliqTheme.spacing.md))
            StatusBadge(config.statusBadge)
        }
        Spacer(Modifier.height(KliqTheme.spacing.md))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = config.amountText,
                style = KliqTheme.typography.amount,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = config.rateText,
                style = KliqTheme.typography.caption,
                color = colors.textSecondary,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = KliqTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = config.dueText,
                style = KliqTheme.typography.caption,
                color = colors.textColorFor(config.dueTone),
            )
            trailing?.invoke(this)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoanCardPreview() {
    KliqTheme {
        LoanCard(
            config = LoanCardConfig(
                id = "1",
                title = "Vehicle Finance",
                amountText = "$42,000",
                rateText = "5.4% interest",
                dueText = "9 days overdue",
                dueTone = Tone.Default,
                typeBadge = BadgeConfig("AUTO", Tone.TypeAuto, "Loan type: auto"),
                statusBadge = BadgeConfig("OVERDUE", Tone.Overdue, "Status: overdue"),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
