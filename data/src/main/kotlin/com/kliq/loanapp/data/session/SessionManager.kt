package com.kliq.loanapp.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/** DataStore-backed session that survives process death and is read off the main thread. */
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dispatchers: DispatcherProvider,
) : SessionRepository {

    private val loggedInKey = booleanPreferencesKey(KEY_IS_LOGGED_IN)

    override val isLoggedIn: Flow<Boolean> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs -> prefs[loggedInKey] ?: false }

    override suspend fun setLoggedIn(value: Boolean) {
        withContext(dispatchers.io) {
            dataStore.edit { it[loggedInKey] = value }
        }
    }

    private companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}
