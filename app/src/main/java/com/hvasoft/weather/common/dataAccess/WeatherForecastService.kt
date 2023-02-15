package com.hvasoft.weather.common.dataAccess

import com.hvasoft.weather.common.entities.WeatherForecastEntity
import com.hvasoft.weather.common.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastService {
    @GET(Constants.ONE_CALL_PATH)
    suspend fun getWeatherForecastByCoordinates(
        @Query(Constants.LATITUDE_PARAM) lat: Double,
        @Query(Constants.LONGITUDE_PARAM) lon: Double,
        @Query(Constants.APP_ID_PARAM) appId: String,
        @Query(Constants.EXCLUDE_PARAM) exclude: String,
        @Query(Constants.UNITS_PARAM) units: String,
        @Query(Constants.LANGUAGE_PARAM) lang: String
    ) : WeatherForecastEntity
}