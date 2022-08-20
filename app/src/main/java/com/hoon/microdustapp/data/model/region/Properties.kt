package com.hoon.microdustapp.data.model.region


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("doName")
    val doName: String,
    @SerializedName("guName")
    val guName: String,
    @SerializedName("viewName")
    val viewName: String
)