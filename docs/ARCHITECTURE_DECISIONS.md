# Architecture Decision Record (ADR)

This document records permanent architectural decisions.

Never rewrite previous decisions.

Only append new ones.

---

## ADR-001

Date

2026-07-16

Decision

Life Reset OS supports exactly ONE active Mission.

Reason

A single Mission encourages clarity and long-term focus.

Status

Accepted

---

## ADR-002

Date

2026-07-16

Decision

A Mission may contain unlimited Goals.

Goals can be added at any time.

Reason

People discover new opportunities while pursuing a Mission.

Status

Accepted

---

## ADR-003

Date

2026-07-16

Decision

Goals belong to Missions.

Reason

Goals represent strategic milestones toward a Mission.

Status

Accepted

---

## ADR-004

Date

2026-07-16

Decision

Tasks belong to Goals.

Never directly to Missions.

Reason

Goals should own execution.

Missions remain strategic.

Status

Accepted

---

## ADR-005

Date

2026-07-16

Decision

Journey is the operational center of the application.

Reason

The Home screen should summarize.

Journey should manage.

Status

Accepted

---

## ADR-006

Date

2026-07-16

Decision

Dashboard cards will eventually deep-link into Journey sections.

Reason

Avoid duplicated functionality.

Status

Accepted

---

## ADR-007

Date

2026-07-16

Decision

MainScaffold remains inside navigation during MVP.

Reason

No functional issue.

Will be moved during Architecture Cleanup.

Status

Accepted

---

## ADR-008

Date

2026-07-16

Decision

Manual dependency injection using AppContainer.

Reason

Keeps MVP simple.

Hilt/Koin may be introduced after MVP if justified.

Status

Accepted

---

## ADR-009

Date

2026-07-16

Decision

Room is the primary source of truth.

Reason

Repositories should expose Room data rather than maintaining duplicate state.

Status

Accepted

---

## ADR-010

Date

2026-07-17

Decision

Tapping a Goal opens a dedicated Goal Detail screen. This screen displays the Goal's information and owns that Goal's Task list and Task CRUD.

Journey remains a high-level operational dashboard: it lists Missions and Goals for orientation, but is not itself a workspace for nested CRUD.

Editing a Goal's own fields (title, description, why, category, priority) and deleting a Goal continue to happen on the existing Goals screen (Create/Edit/Delete), reached from Goal Detail via an explicit Edit action — Goal Detail does not duplicate that form.

Reason

Tasks belong to Goals (ADR-004). As Task CRUD is introduced, it needs a permanent home. Keeping Journey as a summary/orientation view and pushing ownership of a Goal's operational detail (its Tasks) into a dedicated screen matches the Mission → Goals → Tasks hierarchy and avoids Journey becoming an overloaded nested-CRUD surface.

Status

Accepted
---

## ADR-011

Date

2026-07-17

Decision

Task gets a new nullable field, `scheduledDate: Long?`, rather than `dueDate`. "Today's Actions" on the Home Dashboard is defined as Tasks whose `scheduledDate` falls within the current calendar day — not "all incomplete Tasks."

The Home Dashboard's Today's Actions card is tappable and shows "X / Y Completed". Tapping it opens a dedicated Today's Actions screen showing today's scheduled Tasks with their completion toggle and progress, reusing shared Task row UI where possible.

Reason

Life Reset OS is a planning system, not just a deadline tracker — `scheduledDate` reflects when the user intends to work on something, which is a distinct concept from a hard deadline. Naming it this way now avoids a confusing rename or a second field later if deadlines are introduced separately.

Status

Accepted

---

## ADR-012

Date

2026-07-17

Decision

Focus Sessions start only from Today's Actions — there is no global Task picker. A Focus Session is always tied to one of today's scheduled Tasks. If no Tasks are scheduled today, the Focus screen guides the user to Today's Actions instead of allowing arbitrary Task selection.

Duration selection offers presets (15, 25 default, 45, 60 minutes) as single-tap start buttons, plus a custom minutes input. Both paths use the same timer/session logic.

For v0.5.0, SessionStatus supports only COMPLETED and ENDED_EARLY. BROKEN is defined in the enum now for schema forward-compatibility, but nothing sets it yet — true interruption detection (foreground service, lifecycle tracking) is deferred to a future milestone and should not require a schema change when it lands.

Focus Score is computed as `round(actualDurationSeconds / plannedDurationSeconds * 100)`, clamped to 0–100. A naturally COMPLETED session always scores 100. An ENDED_EARLY session scores the percentage of planned time actually spent focused.

The v0.5.0 timer runs in-process via a ViewModel coroutine (no foreground service). It does not survive process death or app kill — only backgrounding within normal Android lifecycle limits. This is an accepted MVP limitation, not an oversight.

Reason

Per the Focus Philosophy (Focus Sessions belong to Tasks; they measure execution, not planning) and the decision to keep Today's Actions as the single source of "what to work on today," letting Focus Sessions pull from anywhere would duplicate that concept. The Focus Score formula isn't specified in LIFE_OS_SPEC.md, so a concrete definition is recorded here rather than left implicit in code.

Status

Accepted

---

## ADR-013

Date

2026-07-18

Decision

Introduce ProgressEngine as the project's first Domain Service for v0.6.0 (Analytics & Progress).

ProgressEngine is coordinated by the ViewModel alongside the Repository layer.

Architecture:

Screen
    ↓
ViewModel
 ↙         ↘
Repository  ProgressEngine
      ↓
     DAO
      ↓
     Room

ProgressEngine is a plain Kotlin class (no Application/Android coupling), constructed from the four existing repositories (Mission, Goal, Task, FocusSession) — the same shape as a Repository being constructed from a DAO. It depends only on Repositories, never on DAOs directly. It performs pure calculation logic only: percentages, streaks, and aggregates. It has no UI and does not decide how anything is displayed. It exposes reactive Flows (via combine()), consistent with ADR-009 (Room is the primary source of truth) and the reactive pattern used everywhere else in the app.

Concrete metric definitions (not fully specified by the v0.6.0 brief, recorded here rather than left implicit in code):

- Mission Progress % = completed Tasks ÷ total Tasks across every Goal under that Mission. 0% if there are no Tasks, never null/undefined.
- Goal Progress % = completed Tasks ÷ total Tasks under that Goal. Same 0%-if-empty rule.
- A "streak day" = any calendar day with at least one completed Task (based on Task.completedAt, device local time).
- Current Streak counts backward from today with a grace period: if today has zero completions yet, today is skipped rather than breaking the streak (today isn't over), and counting starts from yesterday instead.
- Longest Streak = the longest run of consecutive streak-days across all history, not just the recent window.
- Weekly/Monthly boundaries use the device's locale-based first-day-of-week (Calendar.firstDayOfWeek), not a hardcoded Monday or Sunday.
- Average Focus Score is all-time, across every Focus Session ever logged — the only Focus metric in the v0.6.0 list without an explicit "Today"/"This Week" qualifier, unlike Focus Minutes Today/This Week.
- Focus Minutes = actualDurationSeconds summed and floor-divided by 60 (whole minutes).

Two new narrow data-access methods were added to support this (data access, not calculation, so they stay in the Repository/DAO layer, not ProgressEngine): TaskDao.getTasksForMission() (a join, needed for Mission Progress %) and FocusSessionDao.getAllSessions() (needed for Total Focus Sessions / all-time Average Focus Score). Existing single-purpose repository methods (getTodaysTasks(), getTodaysSessions()) were left in place, and generic getTasksScheduledBetween()/getSessionsBetween() passthroughs were added instead of writing near-duplicate Weekly/Monthly-specific queries, per "prefer composition over duplication."

Clarification (2026-07-18): ProgressEngine is a domain service, not a replacement for the Repository layer. The ViewModel is the coordinator — it owns both the Repository dependencies it needs directly (for reads/writes) and the ProgressEngine (for derived metrics), and hands ProgressEngine the same Repository instances it already holds. ProgressEngine is a sibling branch off the ViewModel, not a link in the Repository → DAO → Room chain:

Screen
    ↓
ViewModel
 ↙         ↘
Repository  ProgressEngine
      ↓
     DAO
      ↓
     Room

ProgressEngine's constructor accepting Repository instances (never building or reaching past them) is what makes this the only shape a caller can use it in — a ViewModel cannot obtain a ProgressEngine without already holding the Repositories it was built from.

Future domain services (for example NotificationEngine, AchievementEngine, RecommendationEngine, BackupEngine) should follow this same pattern: a plain Kotlin class, no Application/Android coupling, constructed from Repository instances only, never from DAOs directly, holding pure derived-logic with no UI concerns, and coordinated by the ViewModel alongside whatever Repositories that ViewModel already needs — not layered in place of the Repository layer, and not layered on top of it as a second data-access path.

Reason

Analytics, dashboards, notifications, achievements, and future reporting all need the same derived numbers. Without a dedicated layer, this calculation logic would either get duplicated across ViewModels or leak into composables, both of which this project's architecture explicitly avoids ("business logic must not live inside composables"). A single reactive ProgressEngine gives every future consumer the same answer, computed once, recomputed automatically whenever the underlying Room data changes.

Status

Accepted
