package com.hvasoft.weather.mainModule.model

import com.hvasoft.weather.common.dataAccess.WeatherForecastService
import com.hvasoft.weather.common.entities.WeatherForecastEntity
import com.hvasoft.weather.common.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDatabase {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(WeatherForecastService::class.java)

    suspend fun getWeatherForecastByCoordinates(lat: Double, lon: Double, appId: String, exclude: String,
                                                units: String, lang: String) : WeatherForecastEntity =
        withContext(Dispatchers.IO){
        service.getWeatherForecastByCoordinates(lat, lon, appId, exclude, units, lang)
    }
}