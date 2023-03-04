package com.hvasoft.weather.data

import com.hvasoft.weather.data.model.WeatherForecast
import com.hvasoft.weather.data.model.WeatherForecastProvider
import com.hvasoft.weather.data.network.WeatherService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService,
    private val weatherForecastProvider: WeatherForecastProvider
) {
    suspend fun getWeatherAndForecast(
        lat: Double,
        lon: Double,
        appId: String,
        exclude: String,
        units: String,
        lang: String
    ): WeatherForecast {
        val response = weatherService
            .getWeatherForecastByCoordinates(
                lat,
                lon,
                appId,
                exclude,
                units,
                lang
            )
        weatherForecastProvider.weatherForecast = response
        return response
    }
}