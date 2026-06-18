package com.kliq.loanapp.data.service;

import android.content.Context;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class MockLoanService_Factory implements Factory<MockLoanService> {
  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public MockLoanService_Factory(Provider<Context> contextProvider, Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public MockLoanService get() {
    return newInstance(contextProvider.get(), gsonProvider.get());
  }

  public static MockLoanService_Factory create(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    return new MockLoanService_Factory(contextProvider, gsonProvider);
  }

  public static MockLoanService newInstance(Context context, Gson gson) {
    return new MockLoanService(context, gson);
  }
}
