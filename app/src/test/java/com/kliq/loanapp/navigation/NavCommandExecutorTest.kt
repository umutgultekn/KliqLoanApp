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
 * The Navigator → NavController bridge is the one integration seam the ViewModel unit tests cannot
 * reach (they only assert a command is EMITTED). This verifies a command actually moves the real
 * back stack, via a TestNavHostController over the app's type-safe routes.
 */
@RunWith(RobolectricTestRunner::class)
class NavCommandExecutorTest {

    private fun controller(): TestNavHostController {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val owner = object : LifecycleOwner {
            val registry = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
            override val lifecycle: Lifecycle get() = registry
        }
        return TestNavHostController(context).apply {
            setLifecycleOwner(owner)
            setViewModelStore(ViewModelStore())
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = KliqRoute.Login) {
                composable<KliqRoute.Login> {}
                composable<KliqRoute.Home> {}
            }
        }
    }

    private fun TestNavHostController.routeContains(name: String): Boolean =
        currentBackStackEntry?.destination?.route?.contains(name) == true

    @Test fun `To moves the back stack to the target route and the inclusive popUpTo clears the source`() {
        val nav = controller()
        nav.execute(NavCommand.To(KliqRoute.Home, popUpTo = KliqRoute.Login, inclusive = true))
        assertTrue(nav.routeContains("Home"))
        // inclusive popUpTo = Login must remove Login from the back stack (not just land on Home).
        assertTrue(nav.currentBackStack.value.none { it.destination.route?.contains("Login") == true })
    }

    @Test fun `Back pops to the previous route`() {
        val nav = controller()
        nav.execute(NavCommand.To(KliqRoute.Home))
        nav.execute(NavCommand.Back)
        assertTrue(nav.routeContains("Login"))
    }
}
