# AI HANDOFF

**Project:** Life Reset OS
**Last Updated:** 2026-07-18
**Current Version: v0.7.0

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



ProgressEngine (Domain Service)

NotificationEngine (Domain Service)

ProgressEngine is the first Domain Service in the project.

Domain Services are coordinated by the ViewModel alongside the Repository layer.

Repositories remain the only data-access layer.

Domain Services perform derived business calculations or orchestration only and depend exclusively on Repository instances.

Future domain services should follow the same pattern.



Navigation

All primary application destinations are now first-class NavController routes.

MainScaffold is a UI shell only (Scaffold + Bottom Navigation).

Navigation state is owned by NavController rather than internal MainScaffold state.

This enables notifications, future widgets, shortcuts and external deep-links to navigate directly to any primary destination.

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

✓ Navigation now behaves correctly.

✓ Back button exits the application from Home.

✓ Onboarding is remembered using DataStore.

✓ App reopens directly into Home.

Notifications currently deep-link into top-level navigation destinations.

Internal MainScaffold tabs are not yet individually addressable through NavController.

This navigation refinement is planned for v0.7.1.

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

✓ Progress (Analytics Dashboard — live, reachable from the Home Dashboard's Focus Score card.)

✓ Profile (placeholder)

✓ Notification infrastructure (WorkManager + NotificationEngine)

---

# Home Dashboard

Displays

Current Mission — live (Room)

Active Goals — live count (Room)

Today's Actions — live "X / Y Completed"; tappable, opens Today's Actions screen

Focus Score — live. Displays today's session count and average Focus Score. Accessible from the Home Dashboard and links to the Analytics / Progress screen.

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

NotificationEngine (added v0.7.0)

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

v0.6.0

Analytics & Progress

✓ ProgressEngine

✓ Analytics Dashboard

✓ Progress Screen

✓ Progress Metrics

v0.7.0  Smart Notifications ✅

Smart Notifications

✓ NotificationEngine

✓ Daily Mission Reminder

✓ Today's Actions Reminder

✓ Focus Session Reminder

✓ Weekly Review Reminder

Current

v0.7.1  Navigation Deep Links

v0.8    Backup & Restore

v0.9    Beta Testing

v0.9.x  UI/UX Polish Pass

v1.0    Play Store Release


# Current Development Priority

1.

Habit Streaks (v0.8.3)

Goal:

Build streak tracking on top of the completed Habit system.

Priority

☐ Current streak

☐ Longest streak

☐ Missed-day handling

☐ Streak display in Journey

2.

Future Habit Improvements

☐ Habit reminders

☐ ProgressEngine integration

☐ Habit statistics

3.

Future Features

☐ Backup validation improvements

☐ Achievements

☐ UI/UX Polish Pass (pre-v1.0)

# Next Milestone

v0.8.0 — Backup & Restore

Prerequisite complete:
✓ Navigation Deep Links (v0.7.1)

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