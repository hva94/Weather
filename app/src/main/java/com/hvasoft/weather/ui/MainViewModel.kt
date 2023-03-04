package com.hvasoft.weather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvasoft.weather.R
import com.hvasoft.weather.data.model.WeatherForecast
import com.hvasoft.weather.domain.GetWeatherForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase
) : ViewModel() {

    private val _result = MutableLiveData<WeatherForecast>()
    val result: LiveData<WeatherForecast> = _result

    private val _isLoaded = MutableLiveData<Boolean>()
    val isLoaded: LiveData<Boolean> = _isLoaded

    private val _snackbarMsg = MutableLiveData<Int>()
    val snackbarMsg: LiveData<Int> = _snackbarMsg

    suspend fun getWeatherAndForecast(
        lat: Double, lon: Double, appId: String, exclude: String,
        units: String, lang: String
    ) {
        viewModelScope.launch {
            try {
                _isLoaded.value = false
                val resultServer = getWeatherForecastUseCase(lat, lon, appId, exclude, units, lang)
                _result.value = resultServer
                if (resultServer.hourly.isEmpty())
                    _snackbarMsg.value = R.string.main_error_empty_forecast
            } catch (e: Exception) {
                _snackbarMsg.value = R.string.main_error_server
            } finally {
                _isLoaded.value = true
            }
        }
    }
}