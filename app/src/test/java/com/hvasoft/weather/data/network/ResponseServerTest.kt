package com.hvasoft.weather.data.network

import com.google.gson.Gson
import com.hvasoft.weather.data.model.WeatherForecast
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
class ResponseServerTest {
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `read json not null success`() {
        val reader = JSONFileLoader().loadJSONString("weather_forecast_response_success.json")
        assertThat(reader, `is` (notNullValue()))
    }

    @Test
    fun `read json file success`() {
        val reader = JSONFileLoader().loadJSONString("weather_forecast_response_success.json")
        assertThat(reader, containsString("America/Mexico_City"))
    }

    @Test
    fun `get weatherForecast and check timezone exist`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                JSONFileLoader().loadJSONString("weather_forecast_response_success.json")
                    ?: "{errorCode: 401}"
            )
        mockWebServer.enqueue(response)
        assertThat(response.getBody()?.readUtf8(), containsString("\"timezone\""))
    }

    @Test
    fun `get weatherForecast and check fail response`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                JSONFileLoader().loadJSONString("weather_forecast_response_fail.json")
                    ?: "{errorCode: 401}"
            )
        mockWebServer.enqueue(response)
        assertThat(
            response.getBody()?.readUtf8(),
            containsString("{\"cod\":401, \"message\": \"Please note that using One Call 3.0 requires a separate subscription to the One Call by Call plan. Learn more here https://openweathermap.org/price. If you have a valid subscription to the One Call by Call plan, but still receive this error, then please see https://openweathermap.org/faq#error401 for more info.\"}")
        )
    }

    @Test
    fun `get weatherForecast and check contains hourly list no empty`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                JSONFileLoader().loadJSONString("weather_forecast_response_success.json")
                    ?: "{errorCode: 401}"
            )
        mockWebServer.enqueue(response)
        assertThat(response.getBody()?.readUtf8(), containsString("\"hourly\""))

        val json = Gson().fromJson(
            response.getBody()?.readUtf8() ?: "",
            WeatherForecast::class.java
        )
        assertThat(json.hourly.isEmpty(), `is`(false))
    }
}