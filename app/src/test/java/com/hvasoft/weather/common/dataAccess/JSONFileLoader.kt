package com.hvasoft.weather.common.dataAccess

import com.google.gson.Gson
import com.hvasoft.weather.common.entities.WeatherForecastEntity
import java.io.InputStreamReader

class JSONFileLoader {
    private var jsonStr: String? = null

    fun loadJSONString(file: String): String? {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonStr = loader.readText()
        loader.close()
        return jsonStr
    }

    fun loadWeatherForecastEntity(file: String): WeatherForecastEntity? {
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonStr = loader.readText()
        loader.close()
        return Gson().fromJson(jsonStr, WeatherForecastEntity::class.java)
    }
}