package com.zerosepaisa.liferesetos.util

import java.util.Calendar

object DateUtils {

    /**
     * Start of the current calendar day (device local time), in epoch millis.
     */
    fun startOfToday(): Long = startOfDay(System.currentTimeMillis())

    /**
     * End of the current calendar day (device local time), in epoch millis.
     * Exclusive upper bound for the next day's start.
     */
    fun endOfToday(): Long =
        startOfToday() + 24L * 60L * 60L * 1000L - 1L

    /**
     * Start of the calendar day containing the given timestamp.
     */
    fun startOfDay(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }



    /**
     * Start of the current calendar week (device locale's first day of week),
     * in epoch millis.
     */
    fun startOfWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * End of the current calendar week, in epoch millis.
     */
    fun endOfWeek(): Long =
        startOfWeek() + 7L * 24L * 60L * 60L * 1000L - 1L

    /**
     * Start of the current calendar month, in epoch millis.
     */
    fun startOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * End of the current calendar month, in epoch millis.
     */
    fun endOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startOfMonth()
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return calendar.timeInMillis
    }
}
