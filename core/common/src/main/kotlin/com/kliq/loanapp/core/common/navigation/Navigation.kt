package com.kliq.loanapp.core.common.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/** Type-safe navigation destinations, consumed by Navigation-Compose in the app module. */
@Serializable
sealed interface KliqRoute {
    @Serializable data object Login : KliqRoute

    @Serializable data object Home : KliqRoute
}

/**
 * A SEMANTIC navigation intent emitted by ViewModels — "where to go", not "how". ViewModels know
 * nothing about routes or back-stack mechanics (popUpTo/inclusive); the executor in the app module
 * owns that routing policy. This keeps navigation decisions in the UI layer while ViewModels stay
 * framework-free and testable.
 */
sealed interface NavCommand {
    /** Post-login: go to Home (the executor clears the auth back stack). */
    data object ToHome : NavCommand

    /** Post-logout: return to Login (the executor clears the app back stack). */
    data object ToLogin : NavCommand

    /** Pop the current destination. */
    data object Back : NavCommand
}

/**
 * Single navigation channel. ViewModels depend only on this interface (no NavController), keeping
 * them framework-free and testable; the real implementation lives in the app module.
 */
interface Navigator {
    val commands: Flow<NavCommand>
    suspend fun navigate(command: NavCommand)
}
