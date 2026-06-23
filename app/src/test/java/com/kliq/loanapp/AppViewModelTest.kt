package com.kliq.loanapp

import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.testing.FakeNavigator
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.domain.usecase.ObserveAuthStateUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * The reactive auth gate: the start destination is resolved from the first session value, and every
 * subsequent session change routes through the Navigator — so login/logout (and any future
 * token-expiry) are handled here, not in the screen ViewModels.
 */
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun appViewModel(session: FakeSessionRepository, navigator: FakeNavigator) =
        AppViewModel(ObserveAuthStateUseCase(session), navigator)

    @Test
    fun `start state reflects the initial session`() = runTest {
        val vm = appViewModel(FakeSessionRepository(initial = true), FakeNavigator())
        val state = vm.startState.value
        assertTrue(state is StartState.Ready && state.loggedIn)
    }

    @Test
    fun `logging in reactively routes to Home`() = runTest {
        val session = FakeSessionRepository(initial = false)
        val navigator = FakeNavigator()
        appViewModel(session, navigator)
        session.setLoggedIn(true)
        assertEquals(NavCommand.ToHome, navigator.last)
    }

    @Test
    fun `logging out reactively routes to Login`() = runTest {
        val session = FakeSessionRepository(initial = true)
        val navigator = FakeNavigator()
        appViewModel(session, navigator)
        session.setLoggedIn(false)
        assertEquals(NavCommand.ToLogin, navigator.last)
    }

    @Test
    fun `the initial session value does not trigger navigation`() = runTest {
        val navigator = FakeNavigator()
        appViewModel(FakeSessionRepository(initial = true), navigator)
        assertTrue(navigator.received.isEmpty()) // start destination handles the initial state
    }
}
