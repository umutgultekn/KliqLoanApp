package com.kliq.loanapp.feature.home

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.UiState
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi
import kotlinx.collections.immutable.ImmutableList

/**
 * Home screen state. The mutually-exclusive load phases use the shared [UiState] envelope (so
 * impossible combinations are unrepresentable and the pattern is consistent across data screens);
 * the cross-cutting "chrome" that applies across phases (selected filter, pull-to-refresh, logout
 * dialog) stays flat alongside it.
 */
@Immutable
data class HomeUiState(
    val content: UiState<HomeData> = UiState.Loading,
    val selectedFilter: PortfolioFilter = PortfolioFilter.ALL,
    val isRefreshing: Boolean = false,
    val showLogoutConfirm: Boolean = false,
)

/** The loaded payload of the Home screen — the data behind [UiState.Content]. */
@Immutable
data class HomeData(
    val cards: ImmutableList<LoanCardConfig>,
    val summary: PortfolioSummaryUi,
    /** True when the WHOLE portfolio is empty (vs. just the current filter returning nothing). */
    val portfolioEmpty: Boolean,
)
