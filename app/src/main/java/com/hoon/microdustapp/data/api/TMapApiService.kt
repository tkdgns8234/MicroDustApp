package com.hoon.microdustapp.data.api

import com.hoon.microdustapp.BuildConfig
import com.hoon.microdustapp.data.model.region.SearchRegionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TMapApiService {
    object CATEGORIES {
        const val LEGAL_DONG = "legalDong" // 동 단위 검색
        const val GU_GUN = "gu_gun" // 시, 군, 구 단위 검색
    }

    @GET("/tmap/geofencing/regions?version=1&count=30&searchType=KEYWORD&appKey=${BuildConfig.TMAP_API_KEY}")
    suspend fun searchRegion(
        @Query("categories") categories: String, // 위 CATEGORIES object 참조
        @Query("searchKeyword") searchKeyword: String, // 지역 명 (시, 군, 구, 동)
    ): Response<SearchRegionResponse>
}