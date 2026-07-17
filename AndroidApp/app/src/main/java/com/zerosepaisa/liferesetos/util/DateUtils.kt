package com.zerosepaisa.liferesetos.util

import java.util.Calendar

object DateUtils {

    /**
     * Start of the current calendar day (device local time), in epoch millis.
     */
    fun startOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * End of the current calendar day (device local time), in epoch millis.
     * Exclusive upper bound for the next day's start.
     */
    fun endOfToday(): Long =
        startOfToday() + 24L * 60L * 60L * 1000L - 1L
}
