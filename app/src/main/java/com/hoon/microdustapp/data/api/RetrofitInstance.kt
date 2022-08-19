package com.hoon.microdustapp.data.api

import android.util.Log
import com.hoon.microdustapp.BuildConfig
import com.hoon.microdustapp.data.model.forecast.ForecastItem
import com.hoon.microdustapp.data.model.measure.Grade
import com.hoon.microdustapp.data.model.measure.MeasureResult
import com.hoon.microdustapp.data.model.measuringstation.StationInfo
import com.hoon.microdustapp.data.util.constants.Url.AIR_KOREA_API_BASE_URL
import com.hoon.microdustapp.data.util.constants.Url.KAKAO_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // 위 경도값을 기반으로 TM 좌표를 가져옴
    suspend fun getNearbyMeasuringStation(latitude: Double, longitude: Double): StationInfo? {
        val response = kakaoLocalApiService.getTmCoordinate(latitude, longitude)
        if (response.isSuccessful.not()) return null

        val tmx = response.body()!!.documents.firstOrNull()?.x
        val tmy = response.body()!!.documents.firstOrNull()?.y

        return airKoreaApiService.searchNearbyMeasuringStation(tmx!!, tmy!!)
            .body()
            ?.response
            ?.body
            ?.stationInfos
            ?.firstOrNull()
    }

    suspend fun getMeasureInfo(stationName: String): MeasureResult? {
        val results = airKoreaApiService.getMeasureInfo(stationName)
            .body()
            ?.response
            ?.body
            ?.MeasureResults

        results?.forEach {
            // 필수 정보인 미세먼지, 초미세먼지 정보가 정상적으로 존재하는 경우 해당 데이터 return
            if (it.pm10Grade != null &&
                it.pm25Grade != null &&
                it.pm10Grade != Grade.UNKNOWN &&
                it.pm25Grade != Grade.UNKNOWN) {
                return it
            }
        }
        return null
    }

    suspend fun getForecastInfo(searchDate: String) : List<ForecastItem>? {
        return airKoreaApiService.getForecastInfo(searchDate)
            .body()
            ?.response
            ?.body
            ?.forecastItems
    }

    val kakaoLocalApiService: KaKaoLocalApiService by lazy { getKakaoRetrofit().create(KaKaoLocalApiService::class.java) }

    private fun getKakaoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
    }

    val airKoreaApiService: AirKoreaApiService by lazy { getAirKoreaRetrofit().create(AirKoreaApiService::class.java) }

    private fun getAirKoreaRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
    }

    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        // build 모드 일때만 logging 하도록 설정
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15,TimeUnit.SECONDS)
            .writeTimeout(15,TimeUnit.SECONDS)
            .build()

}