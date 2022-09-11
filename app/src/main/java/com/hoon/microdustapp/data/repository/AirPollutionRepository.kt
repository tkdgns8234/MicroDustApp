package com.hoon.microdustapp.data.repository

import com.hoon.microdustapp.data.model.ForecastModel
import com.hoon.microdustapp.data.response.address.Document
import com.hoon.microdustapp.data.response.forecast.ForecastItem
import com.hoon.microdustapp.data.response.measure.MeasureResult
import com.hoon.microdustapp.data.response.measuringstation.StationInfo

interface AirPollutionRepository {

    suspend fun getNearbyMeasuringStation(latitude: Double, longitude: Double): StationInfo?

    suspend fun getMeasureInfo(stationName: String): MeasureResult?

    suspend fun getForecastInfo(searchDate: String): List<ForecastModel>?

}