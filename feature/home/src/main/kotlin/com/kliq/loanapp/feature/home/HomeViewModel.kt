package com.kliq.loanapp.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.result.Result
import com.kliq.loanapp.core.common.result.asResult
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.model.PortfolioSummary
import com.kliq.loanapp.core.ui.BaseViewModel
import com.kliq.loanapp.core.ui.error.asUiText
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.usecase.GetProcessedLoansUseCase
import com.kliq.loanapp.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getProcessedLoans: GetProcessedLoansUseCase,
    private val mapper: LoanPresentationMapper,
    private val logout: LogoutUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<HomeUiState>(HomeUiState.Loading) {

    private val selectedFilter = savedStateHandle.getStateFlow(KEY_FILTER, PortfolioFilter.ALL)
    private val logoutConfirm = MutableStateFlow(false)
    val showLogoutConfirm: StateFlow<Boolean> = logoutConfirm.asStateFlow()

    init {
        combine(getProcessedLoans().asResult(), selectedFilter, ::buildUiState)
            .onEach { state -> setState { state } }
            .catch { setState { HomeUiState.Error(it.toAppError().asUiText()) } }
            .launchIn(viewModelScope)
    }

    fun onFilterSelected(filter: PortfolioFilter) {
        savedStateHandle[KEY_FILTER] = filter
    }

    fun onLogoutClicked() { logoutConfirm.value = true }
    fun onLogoutDismissed() { logoutConfirm.value = false }

    fun onLogoutConfirmed() {
        logoutConfirm.value = false
        launchSafe { logout() }
    }

    private fun buildUiState(
        result: Result<List<Loan>>,
        filter: PortfolioFilter,
    ): HomeUiState = when (result) {
        Result.Loading -> HomeUiState.Loading
        is Result.Error -> HomeUiState.Error(result.throwable.toAppError().asUiText())
        is Result.Success -> HomeUiState.Content(
            cards = mapper.toCards(result.data.filter(filter::matches)),
            summary = mapper.summary(PortfolioSummary.from(result.data)),
            selectedFilter = filter,
            portfolioEmpty = result.data.isEmpty(),
        )
    }

    internal companion object {
        const val KEY_FILTER = "portfolio_filter"
    }
}
