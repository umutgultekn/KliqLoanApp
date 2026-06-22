# KliqLoanApp

[![CI](https://github.com/umutgultekn/KliqLoanApp/actions/workflows/ci.yml/badge.svg)](https://github.com/umutgultekn/KliqLoanApp/actions/workflows/ci.yml)

A loan-portfolio management app rebuilt from a legacy Android View-system starter into a modern,
multi-module **Jetpack Compose** application following clean-architecture principles.

Two screens — **Login** (validated form with a managed focus chain) and **Home** (the loan portfolio:
summary card, status filtering, a list of loan cards) — backed by a bundled JSON data source (29 loans across
`personal`, `mortgage`, `auto`, `business`).

## Tech stack

- **Kotlin**, **Jetpack Compose** — single `Activity` + composable destinations, recomposition-safe state
- **MVVM + UDF** — `StateFlow<UiState>` collected via `collectAsStateWithLifecycle`, one-shot events via `Channel`
- **Kotlin Coroutines + Flow** — `suspend`, `Dispatchers.IO`, `StateFlow`
- **Hilt** — DI across every layer, including `@IntoMap` strategy multibinding
- **Multi-module Gradle** — a compiler-enforced dependency rule (pure-JVM modules cannot import Android)
- **Design system** — semantic token sets (color, typography, **spacing, shapes, elevation**) delivered
  via `CompositionLocal`; `KliqTheme` projects those tokens onto Material3 roles so Material components
  render in the Kliq palette rather than the default baseline
- **DataStore** (session), **Gson** (JSON), **Navigation-Compose** (type-safe routes), **Timber** (logging)
- **Build logic** — `gradle/libs.versions.toml` version catalog + `build-logic` convention plugins

## Module graph

Dependencies flow inward only; `:core:designsystem` depends only on `:core:common`.

```
:app                  @HiltAndroidApp, single MainActivity, NavHost + NavigatorImpl, auth-gated start
:feature:login        LoginScreen/ViewModel, form state, focus chain, SavedStateHandle
:feature:home         HomeScreen/ViewModel, filtering, summary, retry, pull-to-refresh, logout
:core:ui              BaseViewModel<S>, UiEvent, LoanPresentationMapper, AppError→UiText
:core:designsystem    KliqTheme (color/type/spacing/shape/elevation tokens + Material3 bridge), config-driven components
:core:common          Result/AppError, DispatcherProvider, ValidationRule, Navigator, Tone, UiText, LoanFormatter  (pure JVM)
:core:model           immutable Loan, enums, PortfolioFilter/Summary                                                (pure JVM)
:domain               repository interfaces, use cases, LoanProcessingStrategy + LoanProcessor                      (pure JVM)
:data                 DTO/mapper, MockLoanService (loans.json), repository impls, DataStore, Hilt modules
:core:testing         fakes, fixtures, MainDispatcherRule, TestDispatcherProvider
build-logic           convention plugins (android app/library/compose, jvm library, hilt, feature)
```

## How the assignment's nine tasks are addressed

1. **Compose migration & reusable components** — fully Compose; config-driven `KliqButton(ButtonConfig)`,
   `LoanCard(LoanCardConfig)`, `FormField(FormFieldConfig)` + `KliqCard`/`KliqFilterChip` presets with slot APIs.
2. **MVVM + Navigation** — a `ViewModel` per screen owns logic+state; composables only collect & render;
   a `Navigator` interface (type-safe Navigation-Compose behind it) is the single navigation channel.
3. **SOLID + Strategy** — per-type `LoanProcessingStrategy` resolved via Hilt `@IntoMap`; the global
   lifecycle rules are ordered `LoanStage`s; the starter's nested `if-else` is gone. OCP: a new loan type
   is one new strategy + one binding.
4. **Provider pattern** — semantic color / typography / spacing / shape / elevation tokens via
   `CompositionLocal`, a `ValidationRule` strategy, and a `LoanFormatter` for money/percent. No raw colors,
   `.sp`, hardcoded dp, or `String.format` in screens/ViewModels.
5. **BaseViewModel** — shared loading/error/one-shot-event plumbing + a `launchSafe` that rethrows
   cancellation and routes errors; a single immutable `UiState` per screen.
6. **Form & focus chain** — `FormField` + specialized `EmailFormField`/`PasswordFormField`; email→IME-Next→
   password focus transition; success/error borders and inline messages; field state driven by the ViewModel.
7. **Auth & session** — the assignment's `AuthService` role is the `AuthRepository` interface
   (`login`/`logout`), named for repository-pattern consistency with `LoanRepository`/`SessionRepository`,
   backed by a mock service; plus a DataStore-backed `SessionRepository` exposing `Flow<Boolean>`; app
   launch reads the session for the start destination; logout clears it.
8. **Layered & modular** — the module graph above, wired by Hilt; Domain has zero Android types.
9. **Unit tests** — strategies, lifecycle stages, the processor (ordering/edge cases on real records),
   validation rules, the presentation mapper, both ViewModels (coroutines-test + Turbine + fakes), the
   repository error taxonomy, and a Robolectric test that parses the real `loans.json`.

## UI / UX

- **Design tokens everywhere** — spacing/shape/elevation are tokenized like color/type; no magic dp.
- **Dark theme** — `KliqDarkColors` selected via `isSystemInDarkTheme()`, bridged onto Material3.
- **Motion** — state `Crossfade`, animated list items, `NavHost` fade/slide transitions, `animateContentSize` on fields.
- **Resilience** — error state with retry, pull-to-refresh, content-shaped shimmer skeletons, rich empty states.
- **Edge-to-edge** + IME-aware, scrollable login.
- **Accessibility** — TalkBack grouping & headings, AA-contrast status text tones, ≥48 dp touch targets,
  password-visibility state semantics, RTL-safe padding.
- **Adaptive** — two-column grid on wide screens; dynamic-type-safe (single-line, ellipsized titles).
- **Localized prose** — user-facing text flows through `UiText` string resources + `R.plurals`.

## Notable design decisions

- **Behavior-preserving loan processing.** The new Strategy + stage pipeline reproduces the starter's
  exact semantics (including intra-loop ordering), pinned by golden tests. Latent quirks (non-idempotency,
  terminal `due_in` drift, lenient login validation) are preserved and documented rather than silently
  changed — the task is a refactor, not a behavior change.
- **One navigation mechanism.** ViewModels call `Navigator.navigate(...)`; `UiEvent` carries snackbars only.
- **Error taxonomy.** A sealed `AppError` (asset-missing / parse-failure / io / auth / unknown), each mapped
  to a distinct user message; Gson failures are mapped at the data boundary.
- **R8-ready.** Every `LoanDto` field carries `@SerializedName` plus a consumer keep-rule; minification is
  off in this sample but the rules are in place for when it is enabled.

## Deliberately deferred (next steps)

Out of scope for an architecture-weighted take-home, noted for honesty:

- **Screenshot tests (Paparazzi/Roborazzi)** and **instrumented Compose UI tests on a device** — the Compose
  layer is already exercised by a Robolectric UI test (`LoginScreenTest`); pixel-diffing and on-device runs
  are the next step but add most value with a controlled host/emulator.
- **Baseline Profiles / macrobenchmark** — negligible for a two-screen sample.

## Build & run

```bash
./gradlew :app:installDebug   # build & install
./gradlew test                # all unit tests
./gradlew detekt              # static analysis + ktlint formatting (auto-corrects)
./gradlew koverHtmlReport     # aggregated test-coverage report
```

Requires Android SDK (compileSdk 34), JDK 17+, minSdk 24. The Gradle wrapper is included.

## Code quality

- **detekt** (+ **detekt-formatting**/ktlint) for static analysis & formatting, configured Compose-pragmatically
  in `config/detekt/detekt.yml`, applied to every module; intentional exceptions are `@Suppress`ed with a reason.
- **Kover** for aggregated test coverage across the domain/data/ui/feature modules.
- **`.editorconfig`** as the single source of formatting rules.
- **CI** (GitHub Actions, `.github/workflows/ci.yml`) runs the same gates — `test`, `detekt`,
  `assembleDebug`, `assembleRelease` — on every push and PR.
