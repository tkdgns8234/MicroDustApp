package com.hoon.microdustapp.data.model.address


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("address")
    val address: Address,
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("address_type")
    val addressType: String,
    @SerializedName("road_address")
    val roadAddress: Any,
    @SerializedName("x")
    val x: String,
    @SerializedName("y")
    val y: String
)