package com.kliq.loanapp.feature.portfolio

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PortfolioUiState(
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val cards: ImmutableList<LoanCardConfig> = persistentListOf(),
    val summary: PortfolioSummaryUi = PortfolioSummaryUi.Empty,
    val selectedFilter: PortfolioFilter = PortfolioFilter.ALL,
    /** True when the WHOLE portfolio is empty (vs. just the current filter returning nothing). */
    val portfolioEmpty: Boolean = false,
)
