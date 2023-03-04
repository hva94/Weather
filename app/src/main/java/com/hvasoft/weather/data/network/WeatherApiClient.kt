package com.hvasoft.weather.data.network

import com.hvasoft.weather.data.model.WeatherForecast
import com.hvasoft.weather.core.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiClient {
    @GET(Constants.ONE_CALL_PATH)
    suspend fun getWeatherForecastByCoordinates(
        @Query(Constants.LATITUDE_PARAM) lat: Double,
        @Query(Constants.LONGITUDE_PARAM) lon: Double,
        @Query(Constants.APP_ID_PARAM) appId: String,
        @Query(Constants.EXCLUDE_PARAM) exclude: String,
        @Query(Constants.UNITS_PARAM) units: String,
        @Query(Constants.LANGUAGE_PARAM) lang: String
    ) : WeatherForecast
}