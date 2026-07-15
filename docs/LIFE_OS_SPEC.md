# Life Reset OS Specification

Version: v0.3.0
Status: Draft

---

# Vision

Life Reset OS is not a habit tracker.

It is a personal operating system that helps users rebuild their lives through consistent daily action.

The app is designed around one principle:

> Every day should move the user closer to their Life Mission.

---

# Core Philosophy

Mission
↓
Goals
↓
Daily Actions
↓
Focus Sessions

Every feature in the application must support this hierarchy.

---

# 1. Mission

Purpose:
Represents the user's long-term life purpose.

Rules:
- Only one active Mission at a time.
- Can be edited.
- Can be completed.
- Can be archived.
- Every Goal belongs to exactly one Mission.
- A Mission may contain multiple Goals.

Fields:
- Title
- Why
- Vision (optional)
- Created Date
- Target Date (optional)
- Status

Example:

Mission:
Become financially independent.

Why:
Give my family a better future.

---

# 2. Goals

Purpose:
Medium-term objectives that support the Mission.

Rules:
- Multiple active Goals allowed.
- Belongs to one Mission.
- Can be archived.
- Can be completed.

Fields:
- Title
- Description
- Progress
- Deadline (optional)
- Status

Examples:

- Build Life Reset OS
- Grow YouTube Channel
- Save ₹10,00,000

---

# 3. Daily Actions

Purpose:
Small repeatable actions that move Goals forward.

Rules:
- Belongs to one Goal.
- Can repeat.
- Can be completed each day.

Fields:
- Title
- Repeat Schedule
- Reminder (optional)
- Status

Examples:

- Exercise
- Edit Video
- Read 20 Pages

---

# 4. Focus Sessions

Purpose:
Protected time for deep work.

Rules:
- Belongs to a Daily Action.
- User chooses duration.
- Session can complete successfully.
- Session can end early.
- Session can be broken.

Metrics:
- Duration
- Completion Time
- Focus Score
- Result

Possible Results:
- Completed
- Ended Early
- Broken

---

# Dashboard

The Home Dashboard should display:

- Current Mission
- Goal Progress
- Today's Actions
- Today's Focus Score
- Current Streak

---

# Future Features

- Guardian Mode
- Statistics
- AI Coach
- Notifications
- Cloud Backup
- Widgets

---

# Development Principle

Build functionality first.

Polish later.

Avoid unnecessary complexity.

Every new feature must support the Mission → Goals → Daily Actions → Focus Sessions workflow.