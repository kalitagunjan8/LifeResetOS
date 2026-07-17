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