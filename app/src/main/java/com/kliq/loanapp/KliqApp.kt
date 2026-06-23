package com.kliq.loanapp

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

    // Collected on the STARTED lifecycle (not just composition): the buffered channel holds commands
    // emitted while stopped and delivers them once STARTED again, never mid-teardown.
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
