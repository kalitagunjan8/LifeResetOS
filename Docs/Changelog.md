# Changelog

## v0.1.0

- Project created
- Git initialized
- Android Studio configured
- Documentation added

# Changelog

All notable changes to Life Reset OS will be documented here.

## ADR-015

Task Execution Model

Architecture

- Defined the execution model for Tasks.
- Established the separation between planning and execution.
- Defined Task lifecycle.
- Defined scheduling philosophy.
- Defined notification philosophy.
- Defined Focus integration.
- Established the foundations for Daily Review and Smart Planning.

## v0.8.5

Journey Workspace

- Journey is now the central planning workspace.
- Added dedicated Tasks section.
- Added full Task CRUD from Journey.
- Reused shared Task dialogs across Journey and Goal Detail.
- Home execution flow remains unchanged.

Fixed

- Resolved duplicate LazyColumn key collisions by namespacing keys.
- Refactored Task dialogs into shared reusable components.



## v0.8.4

Habit Reminders

- Added per-Habit daily reminders
- Added reminder enable/disable
- Added reminder time selection
- Reused existing notification infrastructure
- Added HabitReminderScheduler
- Added HabitReminderWorker
- Added reminder indicator in Journey

Infrastructure

- Database schema updated to v7

### Fixed

- Resolved a critical Room database initialization issue where AppContainer created a second AppDatabase instance.
- Eliminated the possibility of unintended destructive migrations caused by multiple Room instances accessing the same database.
- AppContainer now reuses the shared AppDatabase singleton.
- Added temporary database diagnostic logging to monitor future schema migrations.

## v0.8.3

Habit Streaks

- Added HabitStreakEngine domain service
- Added Current Streak calculation
- Added Longest Streak calculation
- Added streak display to Journey → Habits
- Implemented consecutive-day streak logic
- Preserved ADR-013 architecture (derived business logic in domain service)


Infrastructure

- Fixed duplicate Room database initialization
- AppContainer now reuses AppDatabase singleton
- Eliminated unintended destructive migration race condition
- Added temporary Room diagnostic logging

## v0.8.2

Habit Completion

- Added HabitCompletion entity
- Added HabitCompletion DAO
- Added HabitCompletionRepository
- Added one-completion-per-day tracking
- Added persistent daily completion state
- Added checkbox UI for daily completion
- Added completed visual state for Habits

## v0.8.1

- Introduced Habit entity
- Added Room integration for Habits
- Added HabitRepository
- Implemented full Habit CRUD
- Integrated Habits into the Journey screen
- Added Material 3 create/edit dialogs
- Added delete confirmation workflow

## v0.8.0

- Added BackupEngine domain service
- Added Backup & Restore screen
- Implemented JSON backup export using Storage Access Framework
- Implemented full replace restore with relationship preservation
- Added repository/DAO restore helpers
- Added Kotlin Serialization support

## v0.7.1

- Refactored primary navigation to real NavController destinations
- Converted MainScaffold into a UI shell only
- Bottom navigation now driven by NavController state
- Enabled direct notification deep-links to Home, Journey, Focus, Progress and Today's Actions
- Preserved existing back-stack behavior while removing duplicate destinations

## v0.7.0

- Introduced NotificationEngine domain service
- Added WorkManager-based notification scheduling
- Added Daily Mission reminder
- Added Today's Actions reminder
- Added Focus Session reminder
- Added Weekly Review reminder
- Added notification channels and centralized notification helper
- Added notification permission request flow
- Added Application-level AppContainer initialization
- Enabled notification deep-link infrastructure

## v0.6.0

### Added

- Introduced ProgressEngine domain service
- Added reusable analytics calculation layer
- Added dedicated Analytics / Progress screen
- Added mission progress metrics
- Added goal progress metrics
- Added today's, weekly and monthly completion metrics
- Added current and longest streak metrics
- Added focus minutes (today and this week)
- Added average Focus Score metric
- Added per-goal progress breakdown
- Added total completed tasks metric
- Added total focus sessions metric

### Changed

- Home Dashboard Focus Score card now opens the Analytics / Progress screen
- Implemented the first production consumer of ProgressEngine
- Extended repositories to support aggregate progress calculations

### Architecture

- Finalized ADR-013
- Established ProgressEngine as the first Domain Service
- Standardized the Domain Service pattern for future engines

## v0.5.0 — Complete Focus Sessions

### Added

- Focus Session entity
- FocusSessionDao
- FocusSessionRepository
- Focus timer
- Preset durations
- Custom durations
- Session persistence
- Focus Score calculation

### Changed

- Home Dashboard Focus Score now displays live Room data
- Dashboard is now fully backed by application data

### Completed

- Focus Sessions milestone

---

## v0.4 (in progress) - Dynamic Home Dashboard

Confirmed building and running cleanly — 2026-07-17

### Added

- GoalRepository (was referenced in v0.3.2 changelog but missing from codebase; created now)
- GoalRepository registered in AppContainer
- HomeViewModel now streams live Active Goals from Room alongside Mission
- JourneyViewModel (mirrors HomeViewModel pattern)
- JourneyScreen now shows live Mission + list of active Goals (read-only)
- Routes.GOALS route added to navigation
- GoalsViewModel (Create flow only)
- GoalsScreen rebuilt as an Add Goal form (title, description, why, category, priority)
- Journey screen now has a FAB that navigates to the Add Goal screen
- MainScaffold now receives NavHostController to support navigating out of bottom-nav tabs
- Routes.GOALS now accepts an optional goalId argument (Routes.goalsRoute helper)
- GoalsScreen now supports Edit mode: pre-fills from an existing Goal, button reads "Update Goal"
- GoalsViewModel: added loadGoal() and updateGoal() (preserves status/targetDate/createdAt via copy())
- Journey Goal cards are now tappable, opening the Goal in Edit mode
- GoalsViewModel: added deleteGoal()
- GoalsScreen (Edit mode): added Delete Goal button with confirmation dialog before deleting

### Goals CRUD status

Create, Edit, and Delete are all complete as of v0.4. No deferred items remain for Goals CRUD.

---

## v0.5 (in progress) - Tasks

### Architecture

- ADR-010: tapping a Goal now opens a dedicated Goal Detail screen, which owns that Goal's Task list and Task CRUD. Journey stays a high-level dashboard only. Editing/deleting the Goal's own fields still happens on the existing Goals screen, reached from Goal Detail via an Edit button.

### Added

- Task entity (FK to Goal, cascade delete, per ADR-004: Tasks never belong directly to a Mission)
- TaskDao, TaskRepository (same pattern as Goal's)
- AppDatabase: version bumped 1 → 2 to add the tasks table. Added `fallbackToDestructiveMigration()` for both database builders since there's no released install base yet — this must be replaced with a real Migration before release
- Routes.GOAL_DETAIL (route + goalDetailRoute() helper)
- GoalDetailViewModel, GoalDetailScreen: shows Goal info, live Task list, Add Task dialog, complete/incomplete checkbox toggle, Edit Goal button
- Journey's onGoalClick now opens Goal Detail instead of jumping straight into Edit
- GoalDetailViewModel: added updateTask() and deleteTask()
- GoalDetailScreen: task rows are now tappable, opening an Edit Task dialog (pre-filled title, Update button, Delete Task button with confirmation)

### Tasks status

Create, View, Complete toggle, Edit, and Delete are all complete as of v0.5. No deferred items remain for Task CRUD.

---

## v0.4.2 - Home Dashboard Integration (Today's Actions)

### Architecture

- ADR-011: Task gets `scheduledDate: Long?` (not `dueDate` — this is a planning system, not a deadline tracker). "Today's Actions" = Tasks whose scheduledDate falls within the current calendar day. The Home card is tappable and opens a dedicated Today's Actions screen.

### Added

- Task.scheduledDate field
- AppDatabase: version bumped 2 → 3 for the new column (still `fallbackToDestructiveMigration()` — pre-release, no real migration written yet)
- TaskDao.getTasksScheduledBetween(), TaskRepository.getTodaysTasks()
- DateUtils (util/DateUtils.kt): start/end-of-day boundary helpers using java.util.Calendar
- Add/Edit Task dialogs now include a date picker (Material3 DatePickerDialog) to set/clear a Task's scheduledDate
- Extracted TaskRowItem into feature/common/ so it's shared between Goal Detail and the new Today's Actions screen, instead of duplicated
- HomeViewModel now streams today's scheduled Tasks; Home Dashboard's Today's Actions card shows a real "X / Y Completed" count and is tappable
- Routes.TODAYS_ACTIONS, TodaysActionsViewModel, TodaysActionsScreen: shows progress bar + today's scheduled Tasks with a completion toggle (no edit/delete here — that stays owned by Goal Detail per ADR-010)

### Home Dashboard status

Mission, Active Goals, and Today's Actions are all live. Focus Score remains a placeholder, blocked on Focus Sessions (not yet started).

### Fixed

- TaskRowItem now displays a Task's scheduledDate (e.g. "Fri, Jul 17") beneath its title when one is set. Previously the date was saved correctly but never shown anywhere, so there was no visual confirmation it had been set.

---

## v0.5.0 - Focus Sessions

### Architecture

- ADR-012: Focus Sessions start only from Today's Actions (no global Task picker). Duration via presets (15/25 default/45/60 min) or custom input, same logic either way. SessionStatus keeps a BROKEN value for future schema compatibility, but only COMPLETED and ENDED_EARLY are produced this milestone — real interruption detection (foreground service, lifecycle tracking) is deferred. Focus Score = round(actual/planned * 100), clamped 0–100; a COMPLETED session always scores 100.

### Added

- SessionStatus enum (COMPLETED, ENDED_EARLY, BROKEN)
- FocusSession entity (FK to Task, cascade delete)
- FocusSessionDao, FocusSessionRepository (includes getTodaysSessions(), for the future Home Dashboard Focus Score)
- AppDatabase: version bumped 3 → 4 for the new table
- FocusViewModel: 4-stage state machine (SELECT_TASK → SELECT_DURATION → RUNNING → RESULT), in-memory coroutine countdown timer (does not survive process death — accepted MVP limitation per ADR-012)
- FocusScreen: full UI for all four stages, including an empty-state that guides the user to Today's Actions if nothing is scheduled today
- MainScaffold: Focus tab now passes onGoToTodaysActions through to FocusScreen

### Not yet done

- Home Dashboard's Focus Score card is still a placeholder ("No sessions today") — wiring it to real FocusSessionRepository.getTodaysSessions() data is a small follow-up, not yet built
- Real "Broken" session detection (foreground service + lifecycle interruption tracking) — deferred per ADR-012

### Fixed

- Home Dashboard "Active Goals" card no longer hardcoded to "0"; now reflects live goal count

---

## v0.3.2 - Mission & Goal Foundation

Released

2026-07-16

### Added

- Mission entity
- Goal redesign
- GoalStatus enum
- GoalPriority enum
- GoalCategory enum
- MissionRepository
- GoalRepository
- MissionDao
- GoalDao
- Foreign key relationship
- Manual dependency injection via AppContainer

### Fixed

- Room schema issues
- KSP processing errors
- Goal entity redesign
- Navigation persistence

---

## v0.2.0 - Navigation Foundation

### Added

- Splash
- Welcome
- Mission
- Home
- Journey placeholder
- Focus placeholder
- Profile placeholder
- Bottom Navigation
- DataStore onboarding
- MainScaffold

---

## v0.1.0

Initial Android project setup.