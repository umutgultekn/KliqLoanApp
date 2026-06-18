package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onClick: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    val colors = KliqTheme.colors
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = config.title,
                    style = KliqTheme.typography.title,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(config.typeBadge)
                Spacer(Modifier.width(6.dp))
                StatusBadge(config.statusBadge)
            }
            Spacer(Modifier.padding(top = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = config.amountText,
                    style = KliqTheme.typography.title,
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
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = config.dueText,
                    style = KliqTheme.typography.caption,
                    color = colors.colorFor(config.dueTone),
                )
                trailing?.invoke(this)
            }
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
