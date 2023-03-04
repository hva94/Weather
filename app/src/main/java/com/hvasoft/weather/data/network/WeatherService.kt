package com.hvasoft.weather.data.network

import com.hvasoft.weather.data.model.WeatherForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherService @Inject constructor(
    private val apiClient: WeatherApiClient
) {

    suspend fun getWeatherForecastByCoordinates(
        lat: Double,
        lon: Double,
        appId: String,
        exclude: String,
        units: String,
        lang: String
    ): WeatherForecast {
        return withContext(Dispatchers.IO) {
            apiClient.getWeatherForecastByCoordinates(
                lat,
                lon,
                appId,
                exclude,
                units,
                lang
            )
        }
    }
}