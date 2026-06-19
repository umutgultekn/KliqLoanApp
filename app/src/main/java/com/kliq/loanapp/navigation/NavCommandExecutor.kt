package com.kliq.loanapp.navigation

import androidx.navigation.NavController
import com.kliq.loanapp.core.common.navigation.NavCommand

/**
 * Translates a [NavCommand] into a concrete [NavController] action. Extracted out of the composition
 * so the one integration seam the unit suite otherwise can't reach — command → back-stack effect — is
 * directly testable (see NavCommandExecutorTest), and so the collection site in KliqApp stays a thin
 * `commands.collect(navController::execute)`.
 */
fun NavController.execute(command: NavCommand) {
    when (command) {
        is NavCommand.To -> navigate(command.route) {
            command.popUpTo?.let { popUpTarget -> popUpTo(popUpTarget) { inclusive = command.inclusive } }
            launchSingleTop = true
        }
        NavCommand.Back -> popBackStack()
    }
}
