package com.kliq.loanapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import com.kliq.loanapp.feature.login.loginScreen
import com.kliq.loanapp.feature.portfolio.portfolioScreen

@Composable
fun KliqApp(navigator: Navigator, startLoggedIn: Boolean) {
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navigator.commands.collect { command ->
            when (command) {
                is NavCommand.To -> navController.navigate(command.route) {
                    command.popUpTo?.let { popUpTarget ->
                        popUpTo(popUpTarget) { inclusive = command.inclusive }
                    }
                    launchSingleTop = true
                }
                NavCommand.Back -> navController.popBackStack()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (startLoggedIn) KliqRoute.Portfolio else KliqRoute.Login,
    ) {
        loginScreen()
        portfolioScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = KliqTheme.colors.primary)
    }
}
