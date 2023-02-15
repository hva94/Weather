package com.hvasoft.weather.common.utils

import com.hvasoft.weather.common.entities.Weather
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {
    fun getHour(epoch: Long): String = getFormattedTime(epoch, "HH:mm")

    fun getDate(epoch: Long): String = getFormattedTime(epoch, "LLLL dd, yyyy")

    fun getFullDate(epoch: Long): String = getFormattedTime(epoch, "dd-LLL-yy - HH:mm")

    private fun getFormattedTime(epoch: Long, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(epoch * 1000)
    }

    fun getTemp(temp: Double): String = String.format("%.0f°C", temp)

    fun getHumidity(humidity: Int): String = String.format("%d%%", humidity)

    fun getPop(pop: Double): String {
        val popFormatted = (pop * 100).toInt()
        return String.format(" %d%%", popFormatted)
    }

    fun getWeatherMain(weather: List<Weather>?): String {
        return if (weather != null && weather.isNotEmpty()) weather[0].main else "-"
    }

    fun getWeatherDescription(weather: List<Weather>?): String {
        return if (weather != null && weather.isNotEmpty()) weather[0].description else "-"
    }
}