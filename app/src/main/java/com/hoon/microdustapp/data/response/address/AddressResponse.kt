package com.hoon.microdustapp.data.response.address


import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("meta")
    val meta: Meta
)