package com.hvasoft.weather.domain

import com.hvasoft.weather.data.WeatherRepository
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        appId: String,
        exclude: String,
        units: String,
        lang: String
    ) = repository.getWeatherAndForecast(
        lat,
        lon,
        appId,
        exclude,
        units,
        lang
    )
}