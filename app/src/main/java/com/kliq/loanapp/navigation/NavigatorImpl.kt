package com.kliq.loanapp.navigation

import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/** The single navigation channel. Uses suspending send so commands are never silently dropped. */
@Singleton
class NavigatorImpl @Inject constructor() : Navigator {
    private val channel = Channel<NavCommand>(Channel.BUFFERED)
    override val commands: Flow<NavCommand> = channel.receiveAsFlow()
    override suspend fun navigate(command: NavCommand) {
        channel.send(command)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun navigator(impl: NavigatorImpl): Navigator
}
