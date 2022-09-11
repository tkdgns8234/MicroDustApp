package com.hoon.microdustapp.data.api

import com.hoon.microdustapp.BuildConfig
import com.hoon.microdustapp.data.response.forecast.ForecastResponse
import com.hoon.microdustapp.data.response.measure.MeasurementResponse
import com.hoon.microdustapp.data.response.measuringstation.MeasuringStationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {

    @GET("/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?" +
            "serviceKey=${BuildConfig.AIR_KOREA_API_KEY}" +
            "&returnType=json")
    suspend fun searchNearbyMeasuringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MeasuringStationResponse>

    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?" +
            "serviceKey=${BuildConfig.AIR_KOREA_API_KEY}" +
            "&returnType=json" +
            "&dataTerm=month" +
            "&numOfRows=300"+
            "&ver=1.3")
    suspend fun getMeasureInfo(
        @Query("stationName") stationName: String,
    ): Response<MeasurementResponse>

    @GET("/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth?" +
            "serviceKey=${BuildConfig.AIR_KOREA_API_KEY}" +
            "&returnType=json" +
            "&ver=1.1")
    suspend fun getForecastInfo(
        @Query("searchDate") searchDate: String, // e.g) 2022-08-16
    ): Response<ForecastResponse>
}