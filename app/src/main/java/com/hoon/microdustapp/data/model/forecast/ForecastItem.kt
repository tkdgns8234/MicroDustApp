package com.hoon.microdustapp.data.model.forecast


import com.google.gson.annotations.SerializedName

data class ForecastItem(
    @SerializedName("actionKnack")
    val actionKnack: Any,
    @SerializedName("dataTime")
    val dataTime: String,
    @SerializedName("imageUrl1")
    val imageUrl1: String,
    @SerializedName("imageUrl2")
    val imageUrl2: String,
    @SerializedName("imageUrl3")
    val imageUrl3: String,
    @SerializedName("imageUrl4")
    val imageUrl4: String,
    @SerializedName("imageUrl5")
    val imageUrl5: String,
    @SerializedName("imageUrl6")
    val imageUrl6: String,
    @SerializedName("imageUrl7")
    val imageUrl7: String,
    @SerializedName("imageUrl8")
    val imageUrl8: String,
    @SerializedName("imageUrl9")
    val imageUrl9: String,
    @SerializedName("informCause")
    val informCause: String,
    @SerializedName("informCode")
    val informCode: String,
    @SerializedName("informData")
    val informData: String,
    @SerializedName("informGrade")
    val informGrade: String,
    @SerializedName("informOverall")
    val informOverall: String
)