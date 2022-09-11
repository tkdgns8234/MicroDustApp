package com.hoon.microdustapp.data.api

import com.hoon.microdustapp.BuildConfig
import com.hoon.microdustapp.data.response.address.AddressResponse
import com.hoon.microdustapp.data.response.tmcoordinate.TmCoordinateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KaKaoLocalApiService {

    // get url에 고정된 쿼리 파라미터 추가 (output_coord=TM)
    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("/v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinate(
        @Query("y") latitude: Double,
        @Query("x") longitude: Double
    ): Response<TmCoordinateResponse>

    // get url에 고정된 쿼리 파라미터 추가 (output_coord=TM)
    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("/v2/local/search/address.json?size=20")
    suspend fun getAddressFromKeyword(
        @Query("query") query: String,
    ): Response<AddressResponse>
}