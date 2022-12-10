package com.hvasoft.weather.mainModule.model

import com.hvasoft.weather.common.entities.WeatherForecastEntity

class MainRepository {
    private val remoteDatabase = RemoteDatabase()

    suspend fun getWeatherAndForecast(lat: Double, lon: Double, appId: String, exclude: String,
                                      units: String, lang: String) : WeatherForecastEntity =
        remoteDatabase.getWeatherForecastByCoordinates(lat, lon, appId, exclude, units, lang)
}