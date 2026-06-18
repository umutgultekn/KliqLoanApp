package com.kliq.loanapp.feature.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.designsystem.component.LoanCard
import com.kliq.loanapp.core.designsystem.text.asString
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi

fun NavGraphBuilder.portfolioScreen() {
    composable<KliqRoute.Portfolio> { PortfolioRoute() }
}

@Composable
fun PortfolioRoute(viewModel: PortfolioViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PortfolioScreen(
        state = state,
        onFilterSelected = viewModel::onFilterSelected,
        onLogout = viewModel::onLogout,
    )
}

@Composable
fun PortfolioScreen(
    state: PortfolioUiState,
    onFilterSelected: (PortfolioFilter) -> Unit,
    onLogout: () -> Unit,
) {
    val colors = KliqTheme.colors
    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.portfolio_title),
                style = KliqTheme.typography.heading,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onLogout) { Text(stringResource(R.string.portfolio_logout)) }
        }

        when {
            state.isLoading -> CenteredBox { CircularProgressIndicator(color = colors.primary) }
            state.error != null -> CenteredBox {
                Text(state.error.asString(), style = KliqTheme.typography.body, color = colors.statusDefault)
            }
            else -> Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                SummaryCard(state.summary)
                Spacer(Modifier.height(12.dp))
                FilterRow(selected = state.selectedFilter, onFilterSelected = onFilterSelected)
                Spacer(Modifier.height(12.dp))
                if (state.isEmpty || state.cards.isEmpty()) {
                    CenteredBox {
                        Text(stringResource(R.string.portfolio_empty), style = KliqTheme.typography.body, color = colors.textSecondary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(state.cards, key = { it.id }) { card -> LoanCard(config = card) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: PortfolioSummaryUi) {
    val colors = KliqTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.primary,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(summary.totalText, style = KliqTheme.typography.heading, color = colors.onPrimary)
            Spacer(Modifier.height(4.dp))
            Text(summary.countText, style = KliqTheme.typography.body, color = colors.onPrimary)
            Text(summary.avgRateText, style = KliqTheme.typography.caption, color = colors.onPrimary)
        }
    }
}

@Composable
private fun FilterRow(selected: PortfolioFilter, onFilterSelected: (PortfolioFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PortfolioFilter.entries.forEach { filter ->
            FilterChip(
                label = filter.label(),
                isSelected = filter == selected,
                onClick = { onFilterSelected(filter) },
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = KliqTheme.colors
    Text(
        text = label,
        style = KliqTheme.typography.caption,
        color = if (isSelected) colors.onPrimary else colors.textPrimary,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) colors.primary else colors.surface)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun PortfolioFilter.label(): String = stringResource(
    when (this) {
        PortfolioFilter.ALL -> R.string.filter_all
        PortfolioFilter.ACTIVE -> R.string.filter_active
        PortfolioFilter.OVERDUE -> R.string.filter_overdue
        PortfolioFilter.DEFAULT -> R.string.filter_default
        PortfolioFilter.PAID -> R.string.filter_paid
    },
)
