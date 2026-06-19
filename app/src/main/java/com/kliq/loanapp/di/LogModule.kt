package com.kliq.loanapp.di

import com.kliq.loanapp.core.common.log.Logger
import com.kliq.loanapp.log.TimberLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds the app-level [Logger] implementation so lower layers receive it via injection. */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class LogModule {

    @Binds
    @Singleton
    abstract fun logger(impl: TimberLogger): Logger
}
