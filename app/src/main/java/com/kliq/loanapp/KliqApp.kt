package com.kliq.loanapp

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import com.kliq.loanapp.feature.home.homeScreen
import com.kliq.loanapp.feature.login.loginScreen
import com.kliq.loanapp.navigation.execute

@Composable
fun KliqApp(navigator: Navigator, startLoggedIn: Boolean) {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    // The Navigator is the single runtime authority for navigation. Collection is tied to the
    // STARTED lifecycle (not just the composition): commands emitted while the UI is stopped are
    // not consumed mid-teardown — the buffered channel holds them and delivers once STARTED again
    // (buffer-and-deliver-once, not a replay cache).
    LaunchedEffect(navController, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigator.commands.collect(navController::execute)
        }
    }

    NavHost(
        navController = navController,
        // Start destination is resolved ONCE from the session (see AppViewModel); runtime moves go
        // through the Navigator above.
        startDestination = if (startLoggedIn) KliqRoute.Home else KliqRoute.Login,
        enterTransition = { fadeIn() + slideInHorizontally { it / 8 } },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() + slideOutHorizontally { it / 8 } },
    ) {
        loginScreen()
        homeScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = KliqTheme.colors.primary)
    }
}
