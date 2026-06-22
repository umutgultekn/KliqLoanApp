package com.kliq.loanapp.feature.home

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi
import kotlinx.collections.immutable.ImmutableList

/**
 * Home screen state. The mutually-exclusive load phases live in a sealed [content] so impossible
 * combinations (e.g. loading + error + cards at once) are unrepresentable; the cross-cutting
 * "chrome" that applies across phases (selected filter, pull-to-refresh, logout dialog) stays flat.
 */
@Immutable
data class HomeUiState(
    val content: HomeContent = HomeContent.Loading,
    val selectedFilter: PortfolioFilter = PortfolioFilter.ALL,
    val isRefreshing: Boolean = false,
    val showLogoutConfirm: Boolean = false,
)

/** The three mutually-exclusive content phases of the Home screen. */
@Immutable
sealed interface HomeContent {
    data object Loading : HomeContent

    data class Error(val message: UiText) : HomeContent

    data class Content(
        val cards: ImmutableList<LoanCardConfig>,
        val summary: PortfolioSummaryUi,
        /** True when the WHOLE portfolio is empty (vs. just the current filter returning nothing). */
        val portfolioEmpty: Boolean,
    ) : HomeContent
}
