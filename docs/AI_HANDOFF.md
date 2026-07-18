# AI HANDOFF

**Project:** Life Reset OS
**Last Updated:** 2026-07-18
**Current Version:** v0.5.0

---

# Project Vision

Life Reset OS is NOT a habit tracker.

Life Reset OS is a personal operating system designed to help someone rebuild their life from zero.

Everything inside the application should answer one question:

> "What should I do next?"

Every feature should move the user closer to their Mission.

---

# Core Philosophy

Everything flows downward.

Mission
↓
Goals
↓
Tasks (Daily Actions)
↓
Focus Sessions
↓
Progress

This hierarchy is permanent.

---

# Mission Philosophy

A user can have only ONE active Mission.

Examples

- Build Financial Freedom
- Become Physically Strong
- Become a Software Engineer

A Mission represents years of effort.

Changing Missions should be rare.

---

# Goal Philosophy

A Mission may contain unlimited Goals.

Goals may be created:

- during onboarding
- next week
- next month
- years later

Goals are flexible.

Goals represent major milestones toward the Mission.

Examples

Mission

Build Financial Freedom

Goals

• Learn Android Development

• Build YouTube Channel

• Create Emergency Fund

• Launch SaaS

Deleting one Goal must never affect the Mission.

---

# Task Philosophy

Tasks belong to Goals.

Never directly to Missions.

Tasks represent actionable work.

Examples

Watch Compose lesson

Finish Room Database

Publish YouTube Video

---

# Focus Philosophy

Focus Sessions belong to Tasks.

Focus Sessions measure execution rather than planning.

---

# Product Identity

Life Reset OS is NOT

- Habit Tracker
- To-do List
- Calendar
- Notes App

Life Reset OS IS

A Life Operating System.

---

# Current Architecture

MVVM

↓

Repository

↓

DAO

↓

Room Database

Manual Dependency Injection

(AppContainer)

No Hilt/Koin during MVP.

---

# Technology Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room
- StateFlow
- Navigation Compose
- DataStore
- Coroutines

Single Activity Architecture.

---

# Application Structure

Screen

↓

ViewModel

↓

Repository

↓

DAO

↓

Room

Business logic belongs inside ViewModels.

Repositories communicate with Room.

UI should remain as dumb as possible.

---

# Current Navigation

Completed

✓ Splash

✓ Welcome

✓ Mission

✓ MainScaffold

Bottom Navigation

✓ Home

✓ Journey

✓ Focus

✓ Profile

Navigation now behaves correctly.

Back button exits the application from Home.

Onboarding is remembered using DataStore.

App reopens directly into Home.

---

# Current Screens

Implemented

✓ Splash

✓ Welcome

✓ Mission

✓ Home

✓ Journey (live — Mission + Goals list, read-only)

✓ Goals (Add + Edit Goal form; reached via Journey FAB or Goal Detail's Edit button; route: Routes.GOALS with optional goalId)

✓ Goal Detail (Goal info + live Task list, Add Task dialog, complete/incomplete toggle; route: Routes.GOAL_DETAIL; per ADR-010)

✓ Today's Actions (progress + list of today's scheduled Tasks, reuses shared TaskRowItem; route: Routes.TODAYS_ACTIONS; per ADR-011)

✓ Focus (live — Task selection from today's scheduled tasks, duration presets + custom, running timer, result screen; per ADR-012)

✓ Profile (placeholder)

---

# Home Dashboard

Displays

Current Mission — live (Room)

Active Goals — live count (Room)

Today's Actions — live "X / Y Completed"; tappable, opens Today's Actions screen

Focus Score — live (Room)

Displays today's focus statistics from FocusSessionRepository.

If no sessions exist today:

"No sessions today"

Otherwise displays:

"<X> Sessions · <Average Focus Score>% Avg"

Future

Current Mission and Active Goals cards could also deep-link into Journey (not yet done — only Today's Actions is currently tappable).

---

# Journey Screen

Journey will become the operational center of the application.

It will eventually contain

Mission

Goals

Tasks

Progress

Timeline

Current state (v0.4): read-only list view. Displays active Mission and list of active Goals (title, category, priority, status). Tapping a Goal navigates to Goal Detail (per ADR-010), where editing, deleting, and Task management actually happen — Journey itself still does not do inline editing.

The Home Dashboard is only a summary.

---

# Database

Current Entities

Mission

Goal

Task (added v0.5) — FK to Goal, cascade delete; includes scheduledDate (ADR-011)

FocusSession (added v0.5.0) — FK to Task, cascade delete

Current Enums

GoalCategory

GoalPriority

GoalStatus

SessionStatus (added v0.5.0) — COMPLETED, ENDED_EARLY, BROKEN (BROKEN reserved, not yet produced — ADR-012)

Relationship

Mission (1)

↓

Goals (Many)

↓

Tasks (Many)

↓

Focus Sessions (Many)

Foreign Keys implemented at every level.

Room builds successfully. Current schema version: 4 (bumped from 1 as Task and FocusSession were added — see Changelog for the version history). Currently using fallbackToDestructiveMigration() since there's no released install base yet; this must be replaced with real Migrations before release.

---

# Dependency Injection

Manual dependency injection.

AppContainer currently creates

Room Database

MissionRepository

GoalRepository

TaskRepository (added v0.5)

FocusSessionRepository (added v0.5.0)

Future repositories will also be registered here.

---

# Folder Notes

MainScaffold currently exists inside

navigation/

This is intentional.

Future Architecture Cleanup will move it into

ui/scaffold/

Do NOT move until cleanup milestone.

---

# Coding Standards

Prefer StateFlow.

Avoid LiveData unless necessary.

Never hardcode business data.

Keep composables small.

Keep business logic out of UI.

Repositories are the single source of truth.

Prefer reusable components.

Avoid duplicate code.

---

# AI Decision Authority

Claude and ChatGPT MAY decide WITHOUT asking the user

- helper functions
- package organization
- folder structure
- reusable UI
- ViewModel implementation
- repository implementation
- Compose optimization
- Room optimization
- performance improvements
- bug fixes
- refactoring

provided product behaviour does not change.

Claude and ChatGPT MUST ask before changing

Mission philosophy

Navigation flow

Database relationships

Authentication

Monetization

Core terminology

Major UX decisions

---

# Roadmap

Completed

v0.1

Android project

v0.2

Navigation Foundation

v0.3

Mission & Goal Database Foundation

v0.4

Dynamic Home Dashboard (Active Goals + Journey + Goals CRUD landed here, ahead of the original v0.5 Goals Management slot below)

v0.4.2

Home Dashboard Integration — Today's Actions (scheduledDate, ADR-011)

v0.5

Tasks (landed here instead of the originally planned v0.6 slot — Create/View/Complete/Edit/Delete, Goal Detail screen, ADR-010)

v0.5.0

Focus Sessions (landed here instead of the originally planned v0.7 slot — see ADR-012)

Note: actual delivery didn't match the original version numbering below 1:1 — Goals Management, Tasks, and Focus Sessions all shipped earlier than their originally planned v0.5/v0.6/v0.7 slots. Leaving the original plan below for historical reference; the "Completed" list above reflects what's actually true today.

Current

Home Dashboard's Focus Score card — the one remaining item to fully close out v0.4's original Home Dashboard goal (see Current Development Priority, item 1 and item 5)

Upcoming (original plan, not yet reconciled to actual version numbers)

v0.5

Goals Management

v0.6

Tasks

v0.7

Focus Sessions

v0.8

Habits

v0.9

Statistics

v1.0

MVP Release

---

# Current Development Priority

1.

Connect Home Dashboard to live Room data.

✓ Mission — done
✓ Active Goals — done (v0.4)
✓ Today's Actions — done (v0.4.2). scheduledDate added to Task (ADR-011); shows "X / Y Completed", tappable, opens dedicated Today's Actions screen
✓ Focus Score — complete (v0.5.0)

Displays today's session count and average Focus Score from Room.

2.

Build Journey screen.

✓ done (v0.4) — read-only Mission + Goals list

3.

Goals CRUD. ✓ complete (v0.4) — Create, Edit, Delete all done

✓ Create (Add Goal) — done (v0.4), via dedicated Goals screen reached from Journey's FAB
✓ Edit — done (v0.4), reached via Goal Detail's Edit button (was direct tap-to-edit; superseded by ADR-010); reuses GoalsScreen in Edit mode
✓ Delete — done (v0.4), Delete button on Edit Goal screen with confirmation dialog

4.

Tasks. ✓ Create + View + Complete toggle done (v0.4), via new Goal Detail screen (ADR-010)
✓ Edit Task — done (v0.5), tap a task row to open an Edit Task dialog
✓ Delete Task — done (v0.5), Delete button inside Edit Task dialog, with confirmation

5.

Focus Sessions. ✓ complete (v0.5.0), via ADR-012

✓ Task selection — done, from Today's Actions scheduled tasks only (no global picker)
✓ Duration selection — done, presets (15/25 default/45/60 min) + custom minutes input
✓ Running timer — done, in-viewmodel coroutine countdown (does not survive process death — accepted MVP limitation)
✓ Result screen — done, shows status (Completed/Ended Early), duration, Focus Score
✓ Home Dashboard Focus Score

Fully connected to FocusSessionRepository.
☐ Real "Broken" session detection (foreground service + lifecycle interruption tracking) — intentionally deferred per ADR-012; SessionStatus.BROKEN exists in the schema but nothing produces it yet

# Next Milestone

v0.6.0 — Analytics & Progress

Priority order:

1. Progress Engine
2. Analytics Dashboard
3. Progress Visualizations

The Progress Engine should calculate:

- Mission completion
- Goal completion
- Today's completion
- Weekly completion
- Focus minutes today
- Focus minutes this week
- Current streak

Only after the calculations exist should UI dashboards and charts be built.

---

# Non-negotiable Principle

Every feature must reinforce

Mission

↓

Goals

↓

Tasks

↓

Focus

↓

Progress

If a feature does not strengthen this flow,

it probably does not belong inside Life Reset OS.

---

End of handoff.