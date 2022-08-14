package com.hoon.microdustapp.data.api

import com.hoon.microdustapp.BuildConfig
import com.hoon.microdustapp.data.model.measure.MeasureResult
import com.hoon.microdustapp.data.util.constants.Url.AIR_KOREA_API_BASE_URL
import com.hoon.microdustapp.data.util.constants.Url.KAKAO_API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // 위 경도값을 기반으로 TM 좌표를 가져옴
    suspend fun getNearbyMeasuringStation(latitude: Double, longitude: Double): String? {
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
            ?.stationName
    }

    suspend fun getMeasureInfo(stationName: String): MeasureResult? {
        return airKoreaApiService.getMeasureInfo(stationName)
            .body()
            ?.response
            ?.body
            ?.MeasureResults
            ?.firstOrNull()
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
            .build()

}