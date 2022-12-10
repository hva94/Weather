package com.hvasoft.weather.mainModule.view.adapter

import com.hvasoft.weather.common.entities.Forecast

interface OnClickListener {
    fun onClick(forecast: Forecast)
}