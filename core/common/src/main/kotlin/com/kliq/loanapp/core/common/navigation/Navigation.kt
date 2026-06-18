package com.kliq.loanapp.core.common.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/** Type-safe navigation destinations, consumed by Navigation-Compose in the app module. */
@Serializable
sealed interface KliqRoute {
    @Serializable data object Login : KliqRoute
    @Serializable data object Portfolio : KliqRoute
}

/** A navigation intent emitted by ViewModels, translated to NavController calls in the app module. */
sealed interface NavCommand {
    data class To(
        val route: KliqRoute,
        val popUpTo: KliqRoute? = null,
        val inclusive: Boolean = false,
    ) : NavCommand

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
