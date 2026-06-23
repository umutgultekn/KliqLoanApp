package com.kliq.loanapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.format.LoanFormatter
import com.kliq.loanapp.data.datasource.JsonLoanRemoteDataSource
import com.kliq.loanapp.data.datasource.LoanRemoteDataSource
import com.kliq.loanapp.data.dispatcher.DefaultDispatcherProvider
import com.kliq.loanapp.data.repository.AuthRepositoryImpl
import com.kliq.loanapp.data.repository.LoanRepositoryImpl
import com.kliq.loanapp.data.service.AuthService
import com.kliq.loanapp.data.service.MockAuthService
import com.kliq.loanapp.data.session.SessionManager
import com.kliq.loanapp.domain.repository.AuthRepository
import com.kliq.loanapp.domain.repository.LoanRepository
import com.kliq.loanapp.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds @Singleton
    abstract fun loanRepository(impl: LoanRepositoryImpl): LoanRepository

    @Binds @Singleton
    abstract fun authRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun authService(impl: MockAuthService): AuthService

    @Binds @Singleton
    abstract fun sessionRepository(impl: SessionManager): SessionRepository

    @Binds @Singleton
    abstract fun loanRemoteDataSource(impl: JsonLoanRemoteDataSource): LoanRemoteDataSource

    @Binds @Singleton
    abstract fun dispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider

    @Binds @Singleton
    abstract fun loanFormatter(impl: DefaultLoanFormatter): LoanFormatter

    companion object {
        // Tolerant JSON reader for the bundled asset: unknown keys are ignored so the wire format can
        // gain fields without breaking parsing. kotlinx.serialization is reflection-free (R8-safe).
        @Provides @Singleton
        fun json(): Json = Json { ignoreUnknownKeys = true }

        @Provides @Singleton
        fun dataStore(
            @ApplicationContext context: Context,
            dispatchers: DispatcherProvider,
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
                scope = CoroutineScope(dispatchers.io + SupervisorJob()),
                produceFile = { context.preferencesDataStoreFile("kliq_session") },
            )
    }
}
