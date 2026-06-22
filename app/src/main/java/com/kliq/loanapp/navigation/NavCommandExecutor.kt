package com.kliq.loanapp.navigation

import androidx.navigation.NavController
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand

/**
 * Translates a [NavCommand] into a concrete [NavController] action. Extracted out of the composition
 * so the one integration seam the unit suite otherwise can't reach — command → back-stack effect — is
 * directly testable (see NavCommandExecutorTest), and so the collection site in KliqApp stays a thin
 * `commands.collect(navController::execute)`.
 */
fun NavController.execute(command: NavCommand) {
    when (command) {
        // The auth-transition back-stack policy lives HERE (UI layer), not in the ViewModel: each
        // gate transition clears the screen it came from so Back can't return across the auth gate.
        NavCommand.ToHome -> navigate(KliqRoute.Home) {
            popUpTo(KliqRoute.Login) { inclusive = true }
            launchSingleTop = true
        }
        NavCommand.ToLogin -> navigate(KliqRoute.Login) {
            popUpTo(KliqRoute.Home) { inclusive = true }
            launchSingleTop = true
        }
        NavCommand.Back -> popBackStack()
    }
}
