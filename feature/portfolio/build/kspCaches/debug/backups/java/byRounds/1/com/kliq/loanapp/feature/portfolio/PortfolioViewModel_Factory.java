package com.kliq.loanapp.feature.portfolio;

import androidx.lifecycle.SavedStateHandle;
import com.kliq.loanapp.core.common.navigation.Navigator;
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper;
import com.kliq.loanapp.domain.repository.SessionRepository;
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase;
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
public final class PortfolioViewModel_Factory implements Factory<PortfolioViewModel> {
  private final Provider<GetProcessedPortfolioUseCase> getProcessedPortfolioProvider;

  private final Provider<LoanPresentationMapper> mapperProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<Navigator> navigatorProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public PortfolioViewModel_Factory(
      Provider<GetProcessedPortfolioUseCase> getProcessedPortfolioProvider,
      Provider<LoanPresentationMapper> mapperProvider,
      Provider<SessionRepository> sessionRepositoryProvider, Provider<Navigator> navigatorProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.getProcessedPortfolioProvider = getProcessedPortfolioProvider;
    this.mapperProvider = mapperProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.navigatorProvider = navigatorProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public PortfolioViewModel get() {
    return newInstance(getProcessedPortfolioProvider.get(), mapperProvider.get(), sessionRepositoryProvider.get(), navigatorProvider.get(), savedStateHandleProvider.get());
  }

  public static PortfolioViewModel_Factory create(
      Provider<GetProcessedPortfolioUseCase> getProcessedPortfolioProvider,
      Provider<LoanPresentationMapper> mapperProvider,
      Provider<SessionRepository> sessionRepositoryProvider, Provider<Navigator> navigatorProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new PortfolioViewModel_Factory(getProcessedPortfolioProvider, mapperProvider, sessionRepositoryProvider, navigatorProvider, savedStateHandleProvider);
  }

  public static PortfolioViewModel newInstance(GetProcessedPortfolioUseCase getProcessedPortfolio,
      LoanPresentationMapper mapper, SessionRepository sessionRepository, Navigator navigator,
      SavedStateHandle savedStateHandle) {
    return new PortfolioViewModel(getProcessedPortfolio, mapper, sessionRepository, navigator, savedStateHandle);
  }
}
