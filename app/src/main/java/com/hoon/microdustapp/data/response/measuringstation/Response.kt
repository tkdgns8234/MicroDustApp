package com.hoon.microdustapp.data.response.measuringstation


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("body")
    val body: Body,
    @SerializedName("header")
    val header: Header
)