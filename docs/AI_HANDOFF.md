# AI HANDOFF

**Project:** Life Reset OS
**Last Updated:** 2026-07-16
**Current Version:** v0.3.2

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

✓ Goals (Add + Edit Goal form; reached via Journey FAB or tapping a Goal card; route: Routes.GOALS with optional goalId)

✓ Focus (placeholder)

✓ Profile (placeholder)

---

# Home Dashboard

Displays

Current Mission

Active Goals

Today's Actions

Focus Score

Current values are placeholders.

Dashboard cards are intentionally not clickable yet.

Future

Dashboard cards should deep-link into Journey.

---

# Journey Screen

Journey will become the operational center of the application.

It will eventually contain

Mission

Goals

Tasks

Progress

Timeline

Current state (v0.4): read-only. Displays active Mission and list of active Goals (title, category, priority, status). No editing yet — CRUD is a separate future milestone.

The Home Dashboard is only a summary.

---

# Database

Current Entities

Mission

Goal

Current Enums

GoalCategory

GoalPriority

GoalStatus

Relationship

Mission (1)

↓

Goals (Many)

Foreign Keys implemented.

Room builds successfully.

---

# Dependency Injection

Manual dependency injection.

AppContainer currently creates

Room Database

MissionRepository

GoalRepository

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

Current

v0.4

Dynamic Home Dashboard

Upcoming

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
☐ Today's Actions — blocked on Tasks (v0.6)
☐ Focus Score — blocked on Focus Sessions (v0.7)

2.

Build Journey screen.

✓ done (v0.4) — read-only Mission + Goals list

3.

Goals CRUD. ✓ complete (v0.4) — Create, Edit, Delete all done

✓ Create (Add Goal) — done (v0.4), via dedicated Goals screen reached from Journey's FAB
✓ Edit — done (v0.4), tap a Goal card in Journey; reuses GoalsScreen in Edit mode
✓ Delete — done (v0.4), Delete button on Edit Goal screen with confirmation dialog

4.

Tasks.

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