package com.nalombardi.weathercomposechallenge.utils

/**
 * [convertToTime] -
 * Gets a time expressed in Unix, in UTC timezone and retrieves the hour in format HH:mm:ss
 * in the city timezone
 * @param timeZone the timezone of the searched city
 * @return a String with the time in format HH:mm:ss
 */
fun Int?.convertToTime(timeZone: Int?): String{
    var res = ""
    this?.let {
        val utc = timeZone?.timezoneConverter() ?: 0 //convert the timezone in seconds to hours
        val timeUnix = it%86400 //remove the days
        val hour = (timeUnix/3600) + utc //get the hour and add the timezone
        val minUnix = timeUnix%3600 //remove the hours
        val min = minUnix/60 //get the minutes
        val seg = minUnix%60 //get the seconds
        res += if(hour<10) "0$hour:" else "$hour:"
        res += if(min<10) "0$min:" else "$min:"
        res += if(seg<10) "0$seg" else "$seg"
    }
    return res
}

/**
 * [timezoneConverter] -
 * The API is sending the timezone in seconds, so we need to convert it to hours to handle it
 * @return the timezone in hours
 */
fun Int.timezoneConverter(): Int = this/3600

/**
 * [getDirection] -
 * The API is sending the direction of the wind in degrees
 * We want to display a more readable direction
 * @return Cardinal direction depending on the degree
 */
fun Double?.getDirection(): String{
    return this?.let {
        when(it){
            in 348.76..360.00 -> "N"
            in 11.25..33.75 -> "NNE"
            in 33.76..56.25 -> "NE"
            in 56.26..78.75 -> "ENE"
            in 78.76..101.25 -> "E"
            in 101.26..123.75 -> "ESE"
            in 123.76..146.25 -> "SE"
            in 146.26..168.75 -> "SSE"
            in 168.76..191.25 -> "S"
            in 191.26..213.75 -> "SSW"
            in 213.76..236.25 -> "SW"
            in 236.26..258.75 -> "WSW"
            in 258.76..281.25 -> "W"
            in 281.26..303.75 -> "WNW"
            in 303.76..326.25 -> "NW"
            in 326.26..348.75 -> "NNW"
            else -> "N"
        }
    }?:"Unknown"
}