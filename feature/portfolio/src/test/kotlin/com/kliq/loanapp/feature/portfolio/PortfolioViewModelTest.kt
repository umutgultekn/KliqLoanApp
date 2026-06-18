package com.kliq.loanapp.feature.portfolio

import androidx.lifecycle.SavedStateHandle
import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.testing.FakeLoanRepository
import com.kliq.loanapp.core.testing.FakeNavigator
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.core.testing.testLoanProcessor
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val navigator = FakeNavigator()
    private val session = FakeSessionRepository(initial = true)
    private val mapper = LoanPresentationMapper(DefaultLoanFormatter())

    private fun viewModel(loans: List<Loan>): PortfolioViewModel {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.success(loans)), testLoanProcessor())
        return PortfolioViewModel(useCase, mapper, session, navigator, SavedStateHandle())
    }

    // After processing: Consumer Credit stays ACTIVE, Vehicle Finance stays OVERDUE, Commercial Credit stays DEFAULT.
    private val sample = listOf(
        LoanFixtures.consumerCredit,
        LoanFixtures.vehicleFinance,
        LoanFixtures.commercialCredit,
    )

    @Test
    fun `loads all processed loans`() = runTest {
        val vm = viewModel(sample)
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.cards.size)
    }

    @Test
    fun `selecting a status filter narrows the list`() = runTest {
        val vm = viewModel(sample)
        vm.onFilterSelected(PortfolioFilter.ACTIVE)
        assertEquals(1, vm.uiState.value.cards.size)

        vm.onFilterSelected(PortfolioFilter.DEFAULT)
        assertEquals(1, vm.uiState.value.cards.size)

        vm.onFilterSelected(PortfolioFilter.PAID)
        assertEquals(0, vm.uiState.value.cards.size)
    }

    @Test
    fun `logout clears the session and navigates to login`() = runTest {
        val vm = viewModel(sample)
        vm.onLogout()
        assertFalse(session.current)
        assertEquals(
            NavCommand.To(KliqRoute.Login, popUpTo = KliqRoute.Portfolio, inclusive = true),
            navigator.last,
        )
    }
}
