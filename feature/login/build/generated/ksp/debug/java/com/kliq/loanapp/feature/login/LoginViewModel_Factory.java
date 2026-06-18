package com.kliq.loanapp.feature.login;

import androidx.lifecycle.SavedStateHandle;
import com.kliq.loanapp.core.common.navigation.Navigator;
import com.kliq.loanapp.domain.repository.AuthRepository;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<Navigator> navigatorProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<Navigator> navigatorProvider, Provider<SavedStateHandle> savedStateHandleProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.navigatorProvider = navigatorProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(authRepositoryProvider.get(), navigatorProvider.get(), savedStateHandleProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<Navigator> navigatorProvider, Provider<SavedStateHandle> savedStateHandleProvider) {
    return new LoginViewModel_Factory(authRepositoryProvider, navigatorProvider, savedStateHandleProvider);
  }

  public static LoginViewModel newInstance(AuthRepository authRepository, Navigator navigator,
      SavedStateHandle savedStateHandle) {
    return new LoginViewModel(authRepository, navigator, savedStateHandle);
  }
}
