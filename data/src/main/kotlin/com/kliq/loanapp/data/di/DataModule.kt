package com.kliq.loanapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.gson.Gson
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.format.LoanFormatter
import com.kliq.loanapp.data.dispatcher.DefaultDispatcherProvider
import com.kliq.loanapp.data.repository.AuthRepositoryImpl
import com.kliq.loanapp.data.repository.LoanRepositoryImpl
import com.kliq.loanapp.data.service.MockLoanService
import com.kliq.loanapp.data.session.SessionManager
import com.kliq.loanapp.domain.repository.AuthRepository
import com.kliq.loanapp.domain.repository.LoanRepository
import com.kliq.loanapp.domain.repository.SessionRepository
import com.kliq.loanapp.domain.service.LoanService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds @Singleton
    abstract fun loanRepository(impl: LoanRepositoryImpl): LoanRepository

    @Binds @Singleton
    abstract fun authRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun sessionRepository(impl: SessionManager): SessionRepository

    @Binds @Singleton
    abstract fun loanService(impl: MockLoanService): LoanService

    @Binds @Singleton
    abstract fun dispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider

    @Binds @Singleton
    abstract fun loanFormatter(impl: DefaultLoanFormatter): LoanFormatter

    companion object {
        @Provides @Singleton
        fun gson(): Gson = Gson()

        @Provides @Singleton
        fun dataStore(
            @ApplicationContext context: Context,
            dispatchers: DispatcherProvider,
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
                // IO dispatcher comes from the provider, like every other dispatcher (no raw Dispatchers.IO).
                scope = CoroutineScope(dispatchers.io + SupervisorJob()),
                produceFile = { context.preferencesDataStoreFile("kliq_session") },
            )
    }
}
