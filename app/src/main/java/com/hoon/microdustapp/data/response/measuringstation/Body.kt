package com.hoon.microdustapp.data.response.measuringstation


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val stationInfos: List<StationInfo>,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)