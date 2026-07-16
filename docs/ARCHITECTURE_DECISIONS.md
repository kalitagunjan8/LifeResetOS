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