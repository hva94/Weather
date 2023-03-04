package com.hvasoft.weather.data.network

import com.google.gson.Gson
import com.hvasoft.weather.data.model.WeatherForecast
import java.io.InputStreamReader

class JSONFileLoader {
    private var jsonStr: String? = null

    fun loadJSONString(file: String): String? {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonStr = loader.readText()
        loader.close()
        return jsonStr
    }

    fun loadWeatherForecastEntity(file: String): WeatherForecast? {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonStr = loader.readText()
        loader.close()
        return Gson().fromJson(jsonStr, WeatherForecast::class.java)
    }
}