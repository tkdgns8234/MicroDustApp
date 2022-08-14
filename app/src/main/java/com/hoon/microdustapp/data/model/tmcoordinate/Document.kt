package com.hoon.microdustapp.data.model.tmcoordinate


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("x")
    val x: Double,
    @SerializedName("y")
    val y: Double
)