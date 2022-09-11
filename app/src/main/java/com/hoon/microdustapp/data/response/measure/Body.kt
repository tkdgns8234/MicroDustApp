package com.hoon.microdustapp.data.response.measure


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val MeasureResults: List<MeasureResult>,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)