package com.kliq.loanapp.data.session;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider;
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
public final class SessionManager_Factory implements Factory<SessionManager> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public SessionManager_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public SessionManager get() {
    return newInstance(dataStoreProvider.get(), dispatchersProvider.get());
  }

  public static SessionManager_Factory create(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new SessionManager_Factory(dataStoreProvider, dispatchersProvider);
  }

  public static SessionManager newInstance(DataStore<Preferences> dataStore,
      DispatcherProvider dispatchers) {
    return new SessionManager(dataStore, dispatchers);
  }
}
