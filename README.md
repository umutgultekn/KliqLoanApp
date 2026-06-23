# KliqLoanApp

[![CI](https://github.com/umutgultekn/KliqLoanApp/actions/workflows/ci.yml/badge.svg)](https://github.com/umutgultekn/KliqLoanApp/actions/workflows/ci.yml)

A loan-portfolio management app rebuilt from a legacy Android View-system starter into a modern,
multi-module **Jetpack Compose** application following clean-architecture principles.

Two screens — **Login** (validated form with a managed focus chain) and **Home** (the loan portfolio:
summary card, status filtering, a list of loan cards) — backed by a bundled JSON data source (29 loans
spanning four types: `personal`, `business`, `mortgage`, and `auto`).

## Tech stack

- **Kotlin**, **Jetpack Compose** — single `Activity` + composable destinations, recomposition-safe state
- **MVVM + UDF** — `StateFlow<UiState>` collected via `collectAsStateWithLifecycle`, one-shot events via `Channel`
- **Kotlin Coroutines + Flow** — `suspend`, `Dispatchers.IO`, `StateFlow`
- **Hilt** — DI across every layer, including `@IntoMap` strategy multibinding
- **Multi-module Gradle** — a compiler-enforced dependency rule (pure-JVM modules cannot import Android)
- **Design system** — semantic token sets (color, typography, **spacing, shapes, elevation**) delivered
  via `CompositionLocal`; `KliqTheme` projects those tokens onto Material3 roles so Material components
  render in the Kliq palette rather than the default baseline
- **DataStore** (session) and **kotlinx-serialization** — one serialization stack for both `loans.json`
  parsing and **Navigation-Compose** type-safe routes
- **Build logic** — `gradle/libs.versions.toml` version catalog + `build-logic` convention plugins

## Module graph

Dependencies flow inward only; `:core:designsystem` depends only on `:core:common`.

```
:app                  @HiltAndroidApp, single MainActivity, NavHost + NavigatorImpl, auth-gated start
:feature:login        LoginScreen/ViewModel, form state, focus chain (transient — no field persisted)
:feature:home         HomeScreen/ViewModel, filtering, summary, logout (selected filter persisted)
:core:ui              BaseViewModel<S>, UiEvent, LoanPresentationMapper, AppError→UiText
:core:designsystem    KliqTheme (color/type/spacing/shape/elevation tokens + Material3 bridge), config-driven components
:core:common          Result/AppError, DispatcherProvider, ValidationRule, Navigator, Tone, UiText, LoanFormatter  (pure JVM)
:core:model           immutable Loan, enums, PortfolioFilter/Summary                                                (pure JVM)
:domain               repository interfaces, use cases, LoanProcessingStrategy + LoanProcessor                      (pure JVM)
:data                 DTO/mapper, LoanRemoteDataSource (loans.json), repository impls, DataStore, Hilt modules
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
   `.sp`, or `String.format` in screens/ViewModels.
5. **BaseViewModel** — shared loading (reference-counted, overlap-safe) / error / one-shot-event plumbing +
   a `launchSafe` that rethrows cancellation and routes errors; a single immutable `UiState` per screen.
6. **Form & focus chain** — `FormField` + specialized `EmailFormField`/`PasswordFormField`; email→IME-Next→
   password focus transition; success/error borders and inline messages; the submit button is gated on the
   `ValidationRule`s; field state is driven by the ViewModel.
7. **Auth & session** — the assignment's `AuthService` role is the `AuthRepository` interface
   (`login`/`logout`), named for repository-pattern consistency with `LoanRepository`/`SessionRepository`,
   backed by a mock; plus a DataStore-backed `SessionRepository` exposing `Flow<Boolean>`. App launch reads
   the session for the start destination and a reactive auth gate (`AppViewModel`) routes Login↔Home; logout
   clears the session.
8. **Layered & modular** — the module graph above, wired by Hilt; Domain has zero Android types.
9. **Unit tests** — strategies, lifecycle stages, the processor (ordering/edge cases on real records),
   validation rules, the presentation mapper and the data mapper, both ViewModels (coroutines-test + fakes,
   Turbine for Login's one-shot events), the repository error taxonomy, plus Hilt/Robolectric integration
   tests for the strategy multibinding graph and the navigation executor.

## UI / UX

- **Design tokens everywhere** — spacing/shape/elevation are tokenized like color/type across screens and
  components; no magic dp in production composables.
- **Dark theme** — `KliqDarkColors` selected via `isSystemInDarkTheme()`, bridged onto Material3.
- **Resilience** — Home is a sealed `Loading`/`Error`/`Content` state: a centered progress indicator while
  loading, an inline error message, and distinct empty states (empty portfolio vs. a filter with no
  matches, the latter offering "Show all").
- **Edge-to-edge** + IME-aware, scrollable login; `animateContentSize` on form fields.
- **Accessibility** — TalkBack grouping & headings, AA-contrast status text tones, ≥48 dp touch targets,
  password-visibility state semantics, RTL-safe padding.
- **Localized prose** — user-facing text flows through `UiText` string resources + `R.plurals`.

> Visual polish is kept deliberately minimal — the brief weights architecture and code quality over UI flourish.

## Notable design decisions

- **Behavior-preserving loan processing.** The new Strategy + stage pipeline reproduces the starter's
  exact semantics (including intra-loop ordering), pinned by tests. Latent quirks (non-idempotency,
  terminal `due_in` drift, a mock backend that accepts any well-formed credentials) are preserved and
  documented rather than silently changed — the task is a refactor, not a behavior change.
- **Navigation: one system, two triggers.** ViewModels never touch `NavController`. Global/reactive
  transitions (the auth gate — `AppViewModel` observing the session) route Login↔Home via a semantic
  `Navigator` command channel, with back-stack policy (`popUpTo`/`inclusive`) owned by the tested
  `NavCommandExecutor`, not the ViewModels. User-initiated forward navigation (e.g. a future loan
  detail) is hoisted as NavHost lambdas per Google's multi-module guidance, never through a ViewModel.
  `UiEvent` carries snackbars only. Routes are centralized in `core:common` at this scale; a larger
  app would move them to feature API modules.
- **One serialization stack.** kotlinx-serialization powers both type-safe nav routes and `loans.json`
  parsing; being reflection-free it needs no R8/ProGuard keep rules. (The starter parsed JSON with an inline
  `Gson`; that second stack was consolidated out.)
- **Error taxonomy.** A sealed `AppError` (asset-missing / parse-failure / io / auth / unknown), each mapped
  to a distinct user message; JSON parse failures are mapped at the data boundary.
- **State holding.** Login is fully transient — no credential is ever persisted. Home persists only the
  selected filter via `SavedStateHandle`. Action-loading (sign-in) uses the shared, reference-counted
  `BaseViewModel.isLoading` rendered as a button spinner; initial content-loading uses Home's sealed
  `Loading` phase — two different concerns, each modelled with the fitting representation.

## Scaling beyond two screens

The module graph is deliberately a *platform* shape, not a two-screen shortcut — the intent is that new
features slot in without touching existing modules:

- **A new feature** (`:feature:loan-detail`, `:feature:settings`, …) depends only on `:domain` + `:core:*`,
  never on another feature; `:app` is the single place routes are wired, so adding one doesn't ripple.
- **A new data source** swaps behind the repository interface — `loans.json` is read through
  `LoanRemoteDataSource`, so moving to a REST API or Room is a `:data` change while `:domain` and the
  features stay put. The DTO → mapper → `Flow` repository seam is already network-shaped.
- **A new loan type** is one `LoanProcessingStrategy` + one `@IntoMap` binding (Open/Closed); the processor
  and existing strategies are untouched.
- **The dependency rule is compiler-enforced** — `:domain` / `:core:model` / `:core:common` are pure-JVM
  modules, so they physically cannot import Android, Compose, or any data technology.

Intentionally **not** built — because matching scope to the brief is itself a design decision: a separate
`:core:network` / `:core:database` / `:core:analytics`, a per-domain `:data` split, or feature `api/impl`
modules. Those earn their keep at a larger feature count; for a static two-screen brief they would be empty
ceremony. The seams above are where they would attach.

## Deliberately deferred

Scoped out for an architecture-weighted take-home, noted for honesty:

- **Room / offline-first / WorkManager / a real network client** — the data source is a static bundled
  asset (`loans.json`, per the brief), so persistence/sync machinery would be unjustified ceremony (see
  the swap seam above).
- **Compose UI / instrumented tests** (Robolectric or on-device) and **screenshot tests** (Paparazzi/
  Roborazzi) — the unit suite covers logic; UI/pixel testing adds most value with a controlled host/emulator.
- **Baseline Profiles / macrobenchmark** — negligible for a two-screen sample.

## Build & run

```bash
./gradlew :app:installDebug   # build & install
./gradlew test                # all unit tests
./gradlew detekt              # static analysis + ktlint formatting (auto-corrects)
```

Requires Android SDK (compileSdk 34), JDK 17+, minSdk 24. The Gradle wrapper is included.

## Code quality

- **detekt** (+ **detekt-formatting**/ktlint) for static analysis & formatting, configured Compose-pragmatically
  in `config/detekt/detekt.yml`, applied to every module; intentional exceptions are `@Suppress`ed with a reason.
- **`.editorconfig`** as the single source of formatting rules.
- **CI** (GitHub Actions, `.github/workflows/ci.yml`) runs the same gates — `test`, `detekt`,
  `assembleDebug` — on every push and PR.
