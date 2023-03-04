package com.hvasoft.weather.di

import com.hvasoft.weather.core.utils.Constants
import com.hvasoft.weather.data.network.WeatherApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherApiClient(retrofit: Retrofit): WeatherApiClient {
        return retrofit.create(WeatherApiClient::class.java)
    }

}