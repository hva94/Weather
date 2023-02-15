package com.hvasoft.weather.mainModule.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hvasoft.weather.MainCoroutineRule
import com.hvasoft.weather.common.dataAccess.JSONFileLoader
import com.hvasoft.weather.common.dataAccess.WeatherForecastService
import com.hvasoft.weather.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var service: WeatherForecastService

    private val latitude = 19.4342
    private val longitude = -99.1962
    private val apiKey = ""
    private val exclude = ""
    private val units = "metric"
    private val lang = "en"

    companion object {
        private lateinit var retrofit: Retrofit
        private const val baseUrl = "https://api.openweathermap.org/"

        @BeforeClass
        @JvmStatic
        fun setupCommon() {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    @Before
    fun setup() {
        mainViewModel = MainViewModel()
        service = retrofit.create(WeatherForecastService::class.java)
    }

    @Test
    fun checkCurrentWeatherIsNotNullTest() {
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(
                latitude, longitude, apiKey, exclude, units, lang)
            assertThat(result.current, `is` (notNullValue()))
        }
    }

    @Test
    fun checkTimezoneReturnsMexicoCityTest() {
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(
                latitude, longitude, apiKey, exclude, units, lang)
            assertThat(result.timezone, `is` ("America/Mexico_City"))
        }
    }

    @Test
    fun checkErrorResponseWithOnlyCoordinatesTest() {
        runBlocking {
            try {
                service.getWeatherForecastByCoordinates(
                    latitude, longitude, "","", "", "")
            } catch (e: Exception) {
                assertThat(e.localizedMessage, `is` ("HTTP 401 Unauthorized"))
            }
        }
    }

    @Test
    fun checkHourlySizeTest() {
        runBlocking {
            mainViewModel.getWeatherAndForecast(latitude, longitude, apiKey, exclude, units, lang)
            val result = mainViewModel.getResult().getOrAwaitValue()
            assertThat(result.hourly.size, `is` (48))
        }
    }

    @Test
    fun checkHourlySizeRemoteWithLocalTest() {
        runBlocking {
            val remoteResult = service.getWeatherForecastByCoordinates(
                latitude, longitude, apiKey, exclude, units, lang)
            val localResult = JSONFileLoader().loadWeatherForecastEntity(
                "weather_forecast_response_success.json"
            )
            assertThat(localResult?.hourly?.size, `is` (remoteResult.hourly.size))
        }
    }

    @Test
    fun checkTimezoneExistRemoteWithLocalTest() {
        runBlocking {
            val remoteResult = service.getWeatherForecastByCoordinates(
                latitude, longitude, apiKey, exclude, units, lang)
            val localResult = JSONFileLoader().loadWeatherForecastEntity(
                "weather_forecast_response_success.json"
            )
            assertThat(localResult?.timezone, `is` (remoteResult.timezone))
        }
    }

}
