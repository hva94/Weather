package com.hvasoft.weather.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherForecastProvider @Inject constructor() {
    var weatherForecast: WeatherForecast? = null
}