package com.hvasoft.weather.ui.adapter

import com.hvasoft.weather.data.model.Forecast

interface OnClickListener {
    fun onClick(forecast: Forecast)
}