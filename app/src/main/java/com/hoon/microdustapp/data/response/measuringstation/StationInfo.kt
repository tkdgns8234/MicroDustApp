package com.hoon.microdustapp.data.response.measuringstation


import com.google.gson.annotations.SerializedName

data class StationInfo(
    @SerializedName("addr")
    val addr: String,
    @SerializedName("stationName")
    val stationName: String,
    @SerializedName("tm")
    val tm: Double
)