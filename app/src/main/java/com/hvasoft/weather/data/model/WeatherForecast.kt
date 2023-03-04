package com.hvasoft.weather.data.model

data class WeatherForecast(
    val timezone: String,
    val current: Current,
    val hourly: List<Forecast>
)
