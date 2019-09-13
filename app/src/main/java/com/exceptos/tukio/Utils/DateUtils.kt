package com.exceptos.tukio.Utils

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class DateUtils {

    companion object {

        fun resolveDate(day: Int, month: Int, year: Int): String {
            val finalYear = year.toString()
            val finalMonth = when (month) {
                1 -> "January"
                2 -> "February"
                3 -> "March"
                4 -> "April"
                5 -> "May"
                6 -> "June"
                7 -> "July"
                8 -> "August"
                9 -> "September"
                10 -> "October"
                11 -> "November"
                12 -> "December"
                else -> "January"
            }

            val finalDay = when (day) {
                1 -> day.toString() + "st"
                2 -> day.toString() + "nd"
                3 -> day.toString() + "rd"
                else -> day.toString() + "th"
            }

            return "$finalDay of $finalMonth, $finalYear"
        }

        fun resovleTime(hour: Int, min: Int): String {
            val timeOfDay: String
            val finalHour: String

            if (hour >= 12) {
                timeOfDay = "pm"
                finalHour = hour.toString()
            } else {
                timeOfDay = "am"
                finalHour = hour.toString()
            }

            val finalMin = if (min < 10) {
                "0$min"
            } else {
                min.toString()
            }

            return "$finalHour : $finalMin$timeOfDay"
        }

    }

}