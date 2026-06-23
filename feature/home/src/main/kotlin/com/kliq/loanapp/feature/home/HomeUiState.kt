package com.kliq.loanapp.feature.home

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi
import kotlinx.collections.immutable.ImmutableList

/**
 * Home's content state: the three mutually-exclusive load phases as a sealed hierarchy (NiA pattern).
 * The cross-cutting logout-confirm dialog is a separate state holder on the ViewModel.
 */
@Immutable
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Error(val message: UiText) : HomeUiState

    data class Content(
        val cards: ImmutableList<LoanCardConfig>,
        val summary: PortfolioSummaryUi,
        val selectedFilter: PortfolioFilter,
        /** True when the WHOLE portfolio is empty (vs. just the current filter returning nothing). */
        val portfolioEmpty: Boolean,
    ) : HomeUiState
}
