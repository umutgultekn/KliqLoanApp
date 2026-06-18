# KliqLoanApp — Android Starter (Case Study)

This is the **starter codebase** handed to candidates for the Kliq Android Developer case study.
It is the Android counterpart of the iOS UIKit starter and, **by design, contains intentional
architectural and code-quality problems**. The candidate's job is to refactor it according to
[`KliqLoanApp_Assignment_Android.md`](./KliqLoanApp_Assignment_Android.md) (PDF: `KliqLoanApp_Assignment_Android.pdf`).

> ⚠️ This code is intentionally bad. Do not use it as a reference for good Android architecture —
> the assignment is to fix everything described below.

## What the app does

- **Login screen** — logo, e-mail + password fields, "Sign In" button.
- **Home screen** — summary card (total / count / avg. interest), five filter buttons
  (All / Active / Overdue / Default / Paid), and a list of loan cards.
- **Data** — 29 loans in `app/src/main/assets/loans.json` (categories: `personal`, `mortgage`, `auto`, `business`).

## Tech baseline of the starter (deliberately old-school)

- Kotlin + **Android View system** (XML layouts + Activities, ViewBinding)
- **No** Compose, **no** Hilt, **no** coroutines, **no** ViewModel, **no** navigation component
- Single flat `:app` module
- Gson for JSON parsing

## Intentional problems (this is the work to be done)

| Area | Problem in the starter | Target task |
|---|---|---|
| UI toolkit | XML + Activities, imperative binding | Task 1 — migrate to Jetpack Compose |
| Components | Inline `RecyclerView` row binding, no reusable card | Task 1 — reusable config-driven composables |
| Architecture | All logic in `HomeActivity` (filter, format, color) | Task 2 — MVVM + Navigator |
| Loan processing | Deeply nested `if-else` in `LoanRepository` | Task 3 — Strategy Pattern, SOLID |
| Colors/fonts | Hardcoded hex (`#222B45`, `Color.rgb(...)`) everywhere | Task 4 — Color/Font/Validation providers |
| Shared state | No base, no loading/error state | Task 5 — BaseViewModel |
| Form | No validation state, no focus chain | Task 6 — FormField + focus chain |
| Auth | `LoginActivity` starts Home directly, no session | Task 7 — AuthService + session |
| Modularity | Single flat module, no layers | Task 8 — multi-module + Hilt |
| Async | Blocking JSON read on the main thread | Coroutines + Flow |
| Tests | None | Task 9 — unit tests |

## Project layout

```
KliqLoanAppAndroid/
├── KliqLoanApp_Assignment_Android.md / .pdf   # the assignment given to candidates
├── settings.gradle.kts / build.gradle.kts      # single flat module on purpose
├── gradle/wrapper/                             # Gradle 8.9 wrapper
└── app/
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/loans.json                   # data source — keep as-is per the assignment
        ├── java/com/kliq/loanapp/
        │   ├── LoanRepository.kt               # model + LoanService + MockLoanService + nested if-else
        │   ├── LoginActivity.kt                # inline validation, no session
        │   └── HomeActivity.kt                 # filter/summary/color logic + inline adapter
        └── res/layout/                         # activity_login, activity_home, item_loan
```

## Build & run

Open the `KliqLoanAppAndroid/` folder in **Android Studio** (Giraffe+) and run the `app` configuration,
or from the command line:

```bash
./gradlew :app:installDebug
```

Requires Android SDK (compileSdk 34) and JDK 17. The Gradle wrapper (8.9) is included; on first
open Android Studio will sync and download dependencies.
