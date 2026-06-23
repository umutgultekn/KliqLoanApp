package com.kliq.loanapp.feature.home

import androidx.lifecycle.SavedStateHandle
import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.testing.FakeAuthRepository
import com.kliq.loanapp.core.testing.FakeLoanRepository
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.core.testing.testLoanProcessor
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.usecase.GetProcessedLoansUseCase
import com.kliq.loanapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val session = FakeSessionRepository(initial = true)

    // Logout flows through the use case -> FakeAuthRepository -> the shared session, so the
    // session.current assertions hold. The screen no longer navigates (the auth gate does).
    private val logout = LogoutUseCase(FakeAuthRepository(session = session))
    private val mapper = LoanPresentationMapper(DefaultLoanFormatter())

    private fun viewModel(loans: List<Loan>): HomeViewModel {
        val useCase = GetProcessedLoansUseCase(FakeLoanRepository(loans = loans), testLoanProcessor())
        return HomeViewModel(useCase, mapper, logout, SavedStateHandle())
    }

    /** The current state, asserted to be the loaded [HomeUiState.Content] phase. */
    private fun HomeViewModel.loaded(): HomeUiState.Content {
        val state = uiState.value
        assertTrue("expected Content but was $state", state is HomeUiState.Content)
        return state as HomeUiState.Content
    }

    // After processing: Consumer Credit stays ACTIVE, Vehicle Finance stays OVERDUE, Commercial Credit stays DEFAULT.
    private val sample = listOf(
        LoanFixtures.consumerCredit,
        LoanFixtures.vehicleFinance,
        LoanFixtures.commercialCredit,
    )

    @Test
    fun `loads all processed loans into the Content phase`() = runTest {
        val vm = viewModel(sample)
        assertEquals(3, vm.loaded().cards.size)
    }

    @Test
    fun `selecting a status filter narrows the list`() = runTest {
        val vm = viewModel(sample)
        vm.onFilterSelected(PortfolioFilter.ACTIVE)
        assertEquals(1, vm.loaded().cards.size)

        vm.onFilterSelected(PortfolioFilter.DEFAULT)
        assertEquals(1, vm.loaded().cards.size)

        vm.onFilterSelected(PortfolioFilter.PAID)
        assertEquals(0, vm.loaded().cards.size)
    }

    @Test
    fun `summary reflects the whole portfolio regardless of the active filter`() = runTest {
        val vm = viewModel(sample)
        val fullSummary = vm.loaded().summary.countText
        assertEquals(3, (fullSummary as UiText.Plural).quantity)

        vm.onFilterSelected(PortfolioFilter.ACTIVE)
        assertEquals(1, vm.loaded().cards.size)
        // Summary stays on the full portfolio even though the list narrowed to 1.
        assertEquals(fullSummary, vm.loaded().summary.countText)
    }

    @Test
    fun `load failure produces an Error phase`() = runTest {
        val useCase =
            GetProcessedLoansUseCase(FakeLoanRepository(error = AppError.AssetMissing), testLoanProcessor())
        val vm = HomeViewModel(useCase, mapper, logout, SavedStateHandle())
        assertTrue(vm.uiState.value is HomeUiState.Error)
    }

    @Test
    fun `an empty portfolio is flagged`() = runTest {
        val content = viewModel(emptyList()).loaded()
        assertTrue(content.cards.isEmpty())
        assertTrue(content.portfolioEmpty)
    }

    @Test
    fun `restores the persisted filter from SavedStateHandle`() = runTest {
        val useCase = GetProcessedLoansUseCase(FakeLoanRepository(loans = sample), testLoanProcessor())
        val vm = HomeViewModel(
            useCase, mapper, logout,
            SavedStateHandle(mapOf(HomeViewModel.KEY_FILTER to PortfolioFilter.ACTIVE)),
        )
        assertEquals(PortfolioFilter.ACTIVE, vm.loaded().selectedFilter)
        assertEquals(1, vm.loaded().cards.size)
    }

    @Test
    fun `logout clicked shows confirmation without signing out`() = runTest {
        val vm = viewModel(sample)
        vm.onLogoutClicked()
        assertTrue(vm.showLogoutConfirm.value)
        assertTrue(session.current) // still logged in — only confirmed logout clears the session
    }

    @Test
    fun `confirming logout clears the session`() = runTest {
        val vm = viewModel(sample)
        vm.onLogoutClicked()
        vm.onLogoutConfirmed()
        assertFalse(vm.showLogoutConfirm.value)
        // Clearing the session is what routes to Login (via the auth gate); the screen doesn't navigate.
        assertFalse(session.current)
    }
}
