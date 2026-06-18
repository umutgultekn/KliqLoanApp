package com.kliq.loanapp.data.repository;

import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider;
import com.kliq.loanapp.domain.service.LoanService;
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
public final class LoanRepositoryImpl_Factory implements Factory<LoanRepositoryImpl> {
  private final Provider<LoanService> serviceProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public LoanRepositoryImpl_Factory(Provider<LoanService> serviceProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.serviceProvider = serviceProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public LoanRepositoryImpl get() {
    return newInstance(serviceProvider.get(), dispatchersProvider.get());
  }

  public static LoanRepositoryImpl_Factory create(Provider<LoanService> serviceProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new LoanRepositoryImpl_Factory(serviceProvider, dispatchersProvider);
  }

  public static LoanRepositoryImpl newInstance(LoanService service,
      DispatcherProvider dispatchers) {
    return new LoanRepositoryImpl(service, dispatchers);
  }
}
