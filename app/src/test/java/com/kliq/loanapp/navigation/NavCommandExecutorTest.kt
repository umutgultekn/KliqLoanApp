package com.kliq.loanapp.navigation

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The Navigator -> NavController bridge is the one integration seam the ViewModel unit tests cannot
 * reach (they only assert a SEMANTIC command is emitted). This verifies the executor translates each
 * command into the correct real back-stack effect — including the auth-transition popUpTo policy
 * that lives here in the UI layer, not in the ViewModels.
 */
@RunWith(RobolectricTestRunner::class)
class NavCommandExecutorTest {

    private fun controller(start: KliqRoute = KliqRoute.Login): TestNavHostController {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val owner = object : LifecycleOwner {
            val registry = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
            override val lifecycle: Lifecycle get() = registry
        }
        return TestNavHostController(context).apply {
            setLifecycleOwner(owner)
            setViewModelStore(ViewModelStore())
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = start) {
                composable<KliqRoute.Login> {}
                composable<KliqRoute.Home> {}
            }
        }
    }

    private fun TestNavHostController.routeContains(name: String): Boolean =
        currentBackStackEntry?.destination?.route?.contains(name) == true

    private fun TestNavHostController.backStackHas(name: String): Boolean =
        currentBackStack.value.any { it.destination.route?.contains(name) == true }

    @Test fun `ToHome lands on Home and clears the auth back stack`() {
        val nav = controller(start = KliqRoute.Login)
        nav.execute(NavCommand.ToHome)
        assertTrue(nav.routeContains("Home"))
        // The inclusive popUpTo(Login) policy must remove Login so Back can't cross the auth gate.
        assertTrue(!nav.backStackHas("Login"))
    }

    @Test fun `ToLogin lands on Login and clears the app back stack`() {
        val nav = controller(start = KliqRoute.Home)
        nav.execute(NavCommand.ToLogin)
        assertTrue(nav.routeContains("Login"))
        assertTrue(!nav.backStackHas("Home"))
    }

    @Test fun `Back pops the current destination`() {
        val nav = controller(start = KliqRoute.Login)
        nav.navigate(KliqRoute.Home) // raw push, Login stays in the back stack
        nav.execute(NavCommand.Back)
        assertTrue(nav.routeContains("Login"))
    }
}
