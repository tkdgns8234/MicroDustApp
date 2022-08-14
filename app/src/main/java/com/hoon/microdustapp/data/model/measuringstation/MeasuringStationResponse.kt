package com.hoon.microdustapp.data.model.measuringstation


import com.google.gson.annotations.SerializedName

data class MeasuringStationResponse(
    @SerializedName("response")
    val response: Response
)