# Life Reset OS — Project Context

**Last updated:** July 15, 2026
**Updated by:** Claude (Sonnet)

---

## Current Version

**v0.3.0-in-progress** (per the agreed semver plan: v0.1.0 Project Setup → v0.2.0 Navigation → v0.3.0 Goal System)

Navigation (v0.2.0) is complete and committed (`v0.2.0 - Foundation complete`, plus a follow-up `updated the navigation` commit fixing a race condition — see Known Issues/Changelog). Goal System (v0.3.0) is **partially started**: the Room persistence layer exists, but the repository, ViewModel, and UI do not yet exist, and the entity schema itself is not finalized (see Known Issues #2).

---

## Current Architecture

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Pattern:** MVVM (Presentation → ViewModel → Data). **No Repository or domain layer exists yet** — ViewModels currently talk directly to DataStore wrapper classes. This is a deliberate simplification, not an oversight — see Known Issues #3 for the unresolved question of whether to introduce a full `domain/` layer.
- **Navigation:** Navigation Compose, using a **Splash-gate pattern**: `NavHost` always starts at a fixed `Routes.SPLASH` destination. A `LaunchedEffect` inside the Splash composable watches a nullable `StateFlow<Boolean?>` (`null` = still loading from disk) and performs a **one-time** navigation decision once the real value is known, then removes Splash from the back stack via `popUpTo(inclusive = true)`. This pattern exists specifically to avoid re-triggering `NavHost`'s graph reset, which happens if `startDestination` itself is reactive/conditional — this was a real bug we hit and fixed (see Changelog).
- **Persistence:** Jetpack DataStore (Preferences) for simple flags, Room for structured data (Goals).
- **Dependency Injection:** None yet. Manual singleton pattern used for `AppDatabase` (`synchronized` double-checked locking). Preference wrapper classes are instantiated directly by ViewModels (e.g. `MainViewModel` constructs `OnboardingPreferences(application)` itself). **This is a known, temporary pattern** — when Hilt is introduced later, these constructions become `@Provides` functions; no other logic needs to change.
- **Min SDK:** API 28 (Android 9)
- **Target/Compile SDK:** 36
- **Package name:** `com.zerosepaisa.liferesetos`

---

## Current Folder Structure (actual, as of this update)

```
com.zerosepaisa.liferesetos/
├── data/
│   └── local/
│       ├── entity/
│       │   └── Goal.kt              (Goal entity + GoalCategory enum)
│       ├── dao/
│       │   └── GoalDao.kt
│       ├── AppDatabase.kt
│       ├── Converters.kt            (Room TypeConverter for GoalCategory)
│       ├── OnboardingPreferences.kt (DataStore, ACTIVE — wired into MainViewModel)
│       └── UserPreferences.kt       (DataStore, UNUSED — see Known Issues #1)
├── navigation/
│   ├── AppNavigation.kt
│   └── Routes.kt                    (WELCOME, HOME, SPLASH)
├── feature/
│   ├── onboarding/
│   │   └── WelcomeScreen.kt
│   └── home/
│       └── HomeScreen.kt
├── viewmodel/
│   └── MainViewModel.kt
└── MainActivity.kt
```

**⚠️ Divergence from originally planned structure:** ChatGPT's original architecture plan (see chat history) specified:
```
data/
├── database
├── repository
├── model
└── datastore
domain/
├── model
├── repository
└── usecase
```
What actually exists is flatter — everything sits under `data/local/`, and no `domain/` layer exists at all. **This has not been resolved as an intentional decision** — it just happened because implementation moved faster than the architecture doc was updated. See Known Issues #3 for the recommendation and open question.

---

## Dependencies Added or Removed (cumulative, this project)

**Added:**
- `androidx.room:room-runtime:2.8.4`
- `androidx.room:room-ktx:2.8.4`
- `androidx.room:room-compiler:2.8.4` (via KSP)
- `com.google.devtools.ksp` Gradle plugin, version `2.2.10-2.0.2` (must match Kotlin `2.2.10` exactly — KSP versions are tied to Kotlin versions in format `{kotlin}-{ksp}`)
- `androidx.datastore:datastore-preferences:1.1.1` (added earlier, before this update)
- `androidx.navigation:navigation-compose:2.9.3`
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2`
- `androidx.core:core-splashscreen:1.0.1`

**Removed:** None yet.

**Gradle/build config changes:**
- `gradle.properties`: added `android.disallowKotlinSourceSets=false` — required workaround for an **open upstream bug** between KSP and AGP 9.x's "built-in Kotlin" feature ([google/ksp#2729](https://github.com/google/ksp/issues/2729)). Not something to "fix" in this project — revisit only if a future AGP/KSP version changes this behavior.
- `app/build.gradle.kts`: added `ksp { arg("room.schemaLocation", "$projectDir/schemas") }` — Room schema export is required for future migrations. The `schemas/` folder **must be committed to git** going forward.

---

## Features Completed

- ✅ Project scaffolding, Gradle build verified working (AGP 9.2.1, Kotlin 2.2.10)
- ✅ First Git commit and several since (repo has clean `.gitignore`, no leaked build artifacts)
- ✅ Onboarding navigation flow: Splash → Welcome → Home
- ✅ First-launch state persisted via DataStore (`OnboardingPreferences.kt`), survives force-stop/relaunch
- ✅ Room persistence layer scaffolded for Goals: `Goal` entity, `GoalCategory` enum, `GoalDao` (insert/update/delete/query), `AppDatabase` singleton, `Converters`, exported schema v1

## Features Currently In Progress

- 🔶 Goal System (v0.3.0) — Room layer exists, but **paused** pending schema reconciliation (see Known Issues #2). `GoalRepository` and `GoalViewModel` are **not yet started** deliberately, to avoid building on top of fields that may change.

## Features Not Started

- Focus Sessions
- Daily Checklist
- Dashboard
- Guardian Mode (explicitly parked until MVP is complete — do not build, do not redesign)

---

## Known Issues

1. **`UserPreferences.kt` duplicates `OnboardingPreferences.kt`.** Both track onboarding completion, using different DataStore file names (`life_reset_preferences` vs `life_reset_prefs`) and different key semantics (`onboarding_completed` boolean vs `is_first_launch` boolean). `UserPreferences.kt` is **not referenced anywhere** in the codebase currently — it's dead code. It does not appear anywhere in the ~80%-complete ChatGPT chat log reviewed this session, so its origin/intent is unconfirmed. **Recommendation: delete `UserPreferences.kt`, keep `OnboardingPreferences.kt`** (proven, tested, actively wired into `MainViewModel`) — pending explicit confirmation from ChatGPT.

2. **`Goal` entity schema does not match ChatGPT's originally planned schema.** Planned (per `Database.md` discussion in chat history): `id, title, icon, color, createdAt, isArchived`. Actually implemented: `id, title, description, category (GoalCategory enum), targetDate (nullable Long), isCompleted, createdAt`. These reflect different design intents — planned version emphasizes visual customization (icon/color) and archiving; implemented version emphasizes categorization and optional deadlines. **This needs to be explicitly reconciled before building `GoalRepository`/`GoalViewModel`/UI on top of it** — changing entity fields after those layers exist is significantly more expensive.

3. **Package structure divergence (see Folder Structure section above).** Open question: adopt the originally-planned full Clean Architecture (`domain/model`, `domain/repository`, `domain/usecase`) now, or continue with the simpler flat structure already in place. Claude's recommendation: **defer the domain layer** — it's likely over-engineering for a solo-dev MVP, and the project's own dev rules explicitly say "never over-engineer." Renaming `data/local` → `data/database` later is a trivial package move if the flatter structure is kept. **Not yet decided — needs explicit sign-off, not silent continuation either way.**

4. **`Routes.SPLASH` currently renders no visible UI.** It's a blank routing gate (a `LaunchedEffect` with no Composable content) — functional, not cosmetic-complete. ChatGPT's `UI_Roadmap.md` lists "Splash Screen" as UI-build-order item #1, implying it should eventually have real branding/logo content. Currently masked by the native `androidx.core.splashscreen` system splash during the brief gap, so this is low priority, but it is a gap between planned UI and actual implementation.

5. **Non-standard Gradle DSL syntax in `app/build.gradle.kts`** — uses `compileSdk { version = release(36) { minorApiLevel = 1 } }` and `buildTypes.release.optimization { enable = false }` instead of the more common `compileSdk = 36` / `isMinifyEnabled = false`. **Confirmed working** by the developer (builds clean) — left untouched. Flagging only because it's unusual and worth knowing about if a future AGP upgrade changes behavior around this syntax.

---

## Current App Behavior (as of this update, verified by testing)

1. App launch → native splash screen (`core-splashscreen`) → Compose `Splash` route (invisible) → routes to **Welcome** (first launch) or **Home** (already onboarded), decided by the persisted DataStore value.
2. On Welcome screen, tapping **Begin** marks onboarding complete (persisted to disk) and navigates to Home, removing Welcome from the back stack.
3. On Home screen, pressing **Back** exits the app (correct — Welcome is no longer in the stack, and there is no other destination to fall back to).
4. Onboarding state correctly persists across force-stop and relaunch (tested manually on device).
5. No known crashes. Build is clean on Gradle sync and full rebuild.

---

## Next Recommended Tasks (priority order)

1. **Resolve `UserPreferences.kt` vs `OnboardingPreferences.kt`** — confirm with ChatGPT, then delete the unused file.
2. **Reconcile the `Goal` entity schema** against the originally planned `Database.md` fields — decide final field list before writing any repository/ViewModel code against it.
3. **Decide on package structure** — full Clean Architecture (`domain/` layer) now, or defer. Get explicit agreement, don't let it drift further by default.
4. Build `GoalRepository` + `GoalViewModel` — **only after** steps 1–3 are settled.
5. Build Goal creation/list UI.
6. Give the Splash route real UI content per `UI_Roadmap.md` (low priority, cosmetic).
7. Continue MVP build order: Focus Sessions → Daily Checklist → Dashboard.
8. Guardian Mode remains parked — no action until MVP is complete.
