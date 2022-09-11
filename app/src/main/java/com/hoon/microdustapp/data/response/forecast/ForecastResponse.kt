package com.hoon.microdustapp.data.response.forecast


import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("response")
    val response: Response
)