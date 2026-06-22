package com.kliq.loanapp.feature.home

import androidx.lifecycle.SavedStateHandle
import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.testing.FakeAuthRepository
import com.kliq.loanapp.core.testing.FakeLoanRepository
import com.kliq.loanapp.core.testing.FakeNavigator
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.core.testing.testLoanProcessor
import com.kliq.loanapp.core.ui.UiState
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase
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

    private val navigator = FakeNavigator()
    private val session = FakeSessionRepository(initial = true)

    // Logout flows through the use case -> FakeAuthRepository -> the shared session, so the existing
    // session.current assertions still hold.
    private val logout = LogoutUseCase(FakeAuthRepository(session = session))
    private val mapper = LoanPresentationMapper(DefaultLoanFormatter())

    private fun viewModel(loans: List<Loan>): HomeViewModel {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.success(loans)), testLoanProcessor())
        return HomeViewModel(useCase, mapper, logout, navigator, SavedStateHandle())
    }

    /** The current content, asserted to be the loaded [UiState.Content] phase. */
    private fun HomeViewModel.loaded(): HomeData {
        val content = uiState.value.content
        assertTrue("expected Content but was $content", content is UiState.Content)
        return (content as UiState.Content<HomeData>).data
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
            GetProcessedPortfolioUseCase(FakeLoanRepository(Result.failure(AppError.AssetMissing)), testLoanProcessor())
        val vm = HomeViewModel(useCase, mapper, logout, navigator, SavedStateHandle())
        assertTrue(vm.uiState.value.content is UiState.Error)
    }

    @Test
    fun `an empty portfolio is flagged`() = runTest {
        val vm = viewModel(emptyList())
        val content = vm.loaded()
        assertTrue(content.cards.isEmpty())
        assertTrue(content.portfolioEmpty)
    }

    @Test
    fun `restores the persisted filter from SavedStateHandle`() = runTest {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.success(sample)), testLoanProcessor())
        val vm = HomeViewModel(
            useCase, mapper, logout, navigator,
            SavedStateHandle(mapOf(HomeViewModel.KEY_FILTER to PortfolioFilter.ACTIVE)),
        )
        assertEquals(PortfolioFilter.ACTIVE, vm.uiState.value.selectedFilter)
        assertEquals(1, vm.loaded().cards.size)
    }

    @Test
    fun `retry recovers from a load error`() = runTest {
        val repo = FakeLoanRepository(Result.failure(AppError.AssetMissing))
        val vm = HomeViewModel(
            GetProcessedPortfolioUseCase(repo, testLoanProcessor()), mapper, logout, navigator, SavedStateHandle(),
        )
        assertTrue(vm.uiState.value.content is UiState.Error)

        repo.result = Result.success(sample)
        vm.onRetry()

        assertEquals(3, vm.loaded().cards.size)
    }

    @Test
    fun `logout clicked shows confirmation without leaving`() = runTest {
        val vm = viewModel(sample)
        vm.onLogoutClicked()
        assertTrue(vm.uiState.value.showLogoutConfirm)
        assertTrue(session.current) // still logged in — only confirmed logout signs out
        assertTrue(navigator.received.isEmpty())
    }

    @Test
    fun `confirming logout clears the session and navigates to login`() = runTest {
        val vm = viewModel(sample)
        vm.onLogoutClicked()
        vm.onLogoutConfirmed()
        assertFalse(vm.uiState.value.showLogoutConfirm)
        assertFalse(session.current)
        assertEquals(
            NavCommand.To(KliqRoute.Login, popUpTo = KliqRoute.Home, inclusive = true),
            navigator.last,
        )
    }
}
