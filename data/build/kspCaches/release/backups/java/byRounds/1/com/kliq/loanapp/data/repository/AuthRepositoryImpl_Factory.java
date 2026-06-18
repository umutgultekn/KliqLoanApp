package com.kliq.loanapp.data.repository;

import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider;
import com.kliq.loanapp.domain.repository.SessionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public AuthRepositoryImpl_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(sessionRepositoryProvider.get(), dispatchersProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new AuthRepositoryImpl_Factory(sessionRepositoryProvider, dispatchersProvider);
  }

  public static AuthRepositoryImpl newInstance(SessionRepository sessionRepository,
      DispatcherProvider dispatchers) {
    return new AuthRepositoryImpl(sessionRepository, dispatchers);
  }
}
