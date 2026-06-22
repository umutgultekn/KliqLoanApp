package com.kliq.loanapp.domain.repository

import kotlinx.coroutines.flow.Flow

/** Single source of truth for the login session. */
interface SessionRepository {
    val isLoggedIn: Flow<Boolean>
    suspend fun setLoggedIn(value: Boolean)
}
