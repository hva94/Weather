package com.hvasoft.weather.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvasoft.weather.R
import com.hvasoft.weather.common.entities.WeatherForecastEntity
import com.hvasoft.weather.mainModule.model.MainRepository
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val repository = MainRepository()

    private val result = MutableLiveData<WeatherForecastEntity>()
    fun getResult(): LiveData<WeatherForecastEntity> = result

    private val snackbarMsg = MutableLiveData<Int>()
    fun getSnackbarMsg() = snackbarMsg

    private val isLoaded = MutableLiveData<Boolean>()
    fun isLoaded() = isLoaded

    suspend fun getWeatherAndForecast(lat: Double, lon: Double, appId: String, exclude: String,
                                      units: String, lang: String){
        viewModelScope.launch {
            try {
                isLoaded.value = false
                val resultServer = repository.getWeatherAndForecast(lat, lon, appId, exclude, units, lang)
                result.value = resultServer
                if (resultServer.hourly == null || resultServer.hourly.isEmpty())
                    snackbarMsg.value = R.string.main_error_empty_forecast
            } catch (e: Exception) {
                snackbarMsg.value = R.string.main_error_server
            } finally {
                isLoaded.value = true
            }
        }
    }
}