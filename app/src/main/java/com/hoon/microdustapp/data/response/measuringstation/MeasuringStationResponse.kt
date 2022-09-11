package com.hoon.microdustapp.data.response.measuringstation


import com.google.gson.annotations.SerializedName

data class MeasuringStationResponse(
    @SerializedName("response")
    val response: Response
)