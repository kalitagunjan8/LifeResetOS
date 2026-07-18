package com.zerosepaisa.liferesetos.navigation

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val SPLASH = "splash"
    const val MISSION = "mission"

    const val GOAL_ID_ARG = "goalId"
    const val GOALS = "goals?goalId={goalId}"

    const val GOAL_DETAIL = "goalDetail/{goalId}"

    const val TODAYS_ACTIONS = "todaysActions"

    const val PROGRESS = "progress"

    /**
     * Builds a navigable route string.
     * Pass no goalId (or null) for Create mode.
     * Pass a goalId for Edit mode.
     */
    fun goalsRoute(goalId: Long? = null): String =
        if (goalId != null) "goals?goalId=$goalId" else "goals"

    /**
     * Route to the Goal Detail screen (owns that Goal's Task list, per ADR-010).
     */
    fun goalDetailRoute(goalId: Long): String =
        "goalDetail/$goalId"
}