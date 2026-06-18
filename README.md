# KliqLoanApp

A loan-portfolio management app rebuilt from a legacy Android View-system starter into a modern,
multi-module **Jetpack Compose** application following clean-architecture principles.

Two screens — **Login** (validated form with a managed focus chain) and **Portfolio** (summary card,
status filtering, a list of loan cards) — backed by a bundled JSON data source (29 loans across
`personal`, `mortgage`, `auto`, `business`).

## Tech stack

- **Kotlin**, **Jetpack Compose** — single `Activity` + composable destinations, recomposition-safe state
- **MVVM + UDF** — `StateFlow<UiState>` collected via `collectAsStateWithLifecycle`, one-shot events via `Channel`
- **Kotlin Coroutines + Flow** — `suspend`, `Dispatchers.IO`, `StateFlow`
- **Hilt** — DI across every layer, including `@IntoMap` strategy multibinding
- **Multi-module Gradle** — a compiler-enforced dependency rule (pure-JVM modules cannot import Android)
- **DataStore** (session), **Gson** (JSON), **Navigation-Compose** (type-safe routes), **Timber** (logging)
- **Build logic** — `gradle/libs.versions.toml` version catalog + `build-logic` convention plugins

## Module graph

Dependencies flow inward only; `:core:designsystem` is a strict leaf.

```
:app                  @HiltAndroidApp, single MainActivity, NavHost + NavigatorImpl, auth-gated start
:feature:login        LoginScreen/ViewModel, form state, focus chain, SavedStateHandle
:feature:portfolio    PortfolioScreen/ViewModel, filtering, summary, logout
:core:ui              BaseViewModel<S>, UiEvent, LoanPresentationMapper, AppError→UiText
:core:designsystem    KliqTheme (color/type providers + CompositionLocals), config-driven components
:core:common          Result/AppError, DispatcherProvider, ValidationRule, Navigator, Tone, LoanFormatter  (pure JVM)
:core:model           immutable Loan, enums, PortfolioFilter/Summary                                        (pure JVM)
:domain               repository interfaces, use cases, LoanProcessingStrategy + LoanProcessor              (pure JVM)
:data                 DTO/mapper, MockLoanService (loans.json), repository impls, DataStore, Hilt modules
:core:testing         fakes, fixtures, MainDispatcherRule, TestDispatcherProvider
build-logic           convention plugins (android app/library/compose, jvm library, hilt, feature)
```

## How the assignment's nine tasks are addressed

1. **Compose migration & reusable components** — fully Compose; config-driven `KliqButton(ButtonConfig)`,
   `LoanCard(LoanCardConfig)`, `FormField(FormFieldConfig)` with slot APIs.
2. **MVVM + Navigation** — a `ViewModel` per screen owns logic+state; composables only collect & render;
   a `Navigator` interface (type-safe Navigation-Compose behind it) is the single navigation channel.
3. **SOLID + Strategy** — per-type `LoanProcessingStrategy` resolved via Hilt `@IntoMap`; the global
   lifecycle rules are ordered `LoanStage`s; the starter's nested `if-else` is gone. OCP: a new loan type
   is one new strategy + one binding.
4. **Provider pattern** — semantic `ColorProvider`/`Typography` via `CompositionLocal`, a `ValidationRule`
   strategy, and a `LoanFormatter` for money/percent. No raw colors, `.sp`, or `String.format` in screens/VMs.
5. **BaseViewModel** — shared loading/error/one-shot-event plumbing + a `launchSafe` that rethrows
   cancellation and routes errors; a single immutable `UiState` per screen.
6. **Form & focus chain** — `FormField` + specialized `EmailFormField`/`PasswordFormField`; email→IME-Next→
   password focus transition; success/error borders and inline messages; field state driven by the ViewModel.
7. **Auth & session** — `AuthService`/`AuthRepository`, a DataStore-backed `SessionRepository` exposing
   `Flow<Boolean>`; app launch reads the session for the start destination; logout clears it.
8. **Layered & modular** — the module graph above, wired by Hilt; Domain has zero Android types.
9. **Unit tests** — strategies, lifecycle stages, the processor (ordering/edge cases on real records),
   validation rules, the presentation mapper, both ViewModels (coroutines-test + Turbine + fakes), the
   repository error taxonomy, and a Robolectric test that parses the real `loans.json`.

## Notable design decisions

- **Behavior-preserving loan processing.** The new Strategy + stage pipeline reproduces the starter's
  exact semantics (including intra-loop ordering), pinned by golden tests. Latent quirks (non-idempotency,
  terminal `due_in` drift, lenient login validation) are preserved and documented rather than silently
  changed — the task is a refactor, not a behavior change.
- **One navigation mechanism.** ViewModels call `Navigator.navigate(...)`; `UiEvent` carries snackbars only.
- **Error taxonomy.** A sealed `AppError` (asset-missing / parse-failure / empty / io / auth / unknown),
  each mapped to a distinct user message; Gson failures are mapped at the data boundary.
- **R8 safety.** Every `LoanDto` field carries `@SerializedName`, plus a consumer keep-rule.

## Build & run

```bash
./gradlew :app:installDebug   # build & install
./gradlew test                # all unit tests
```

Requires Android SDK (compileSdk 34), JDK 17+, minSdk 24. The Gradle wrapper is included.
