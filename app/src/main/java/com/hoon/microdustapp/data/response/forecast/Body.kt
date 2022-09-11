package com.hoon.microdustapp.data.response.forecast


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val forecastItems: List<ForecastItem>,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)