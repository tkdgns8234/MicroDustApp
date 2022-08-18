package com.hoon.microdustapp.data.model.forecast


import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("response")
    val response: Response
)