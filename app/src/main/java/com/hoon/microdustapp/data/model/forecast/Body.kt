package com.hoon.microdustapp.data.model.forecast


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