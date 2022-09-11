package com.hoon.microdustapp.data.response.measure


import com.google.gson.annotations.SerializedName

data class MeasurementResponse(
    @SerializedName("response")
    val response: Response
)