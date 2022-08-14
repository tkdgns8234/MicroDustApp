package com.hoon.microdustapp.data.model.measure


import com.google.gson.annotations.SerializedName

data class MeasurementResponse(
    @SerializedName("response")
    val response: Response
)