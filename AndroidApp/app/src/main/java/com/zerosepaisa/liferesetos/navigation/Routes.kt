package com.zerosepaisa.liferesetos.navigation

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val SPLASH = "splash"
    const val MISSION = "mission"

    const val JOURNEY = "journey"
    const val FOCUS = "focus"
    const val PROFILE = "profile"

    const val GOAL_ID_ARG = "goalId"
    const val GOALS = "goals?goalId={goalId}"

    const val GOAL_DETAIL = "goalDetail/{goalId}"

    const val TODAYS_ACTIONS = "todaysActions"

    const val PROGRESS = "progress"

    fun goalsRoute(goalId: Long? = null): String =
        if (goalId != null) "goals?goalId=$goalId" else "goals"

    fun goalDetailRoute(goalId: Long): String =
        "goalDetail/$goalId"
}