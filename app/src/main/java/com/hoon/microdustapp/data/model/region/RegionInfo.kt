package com.hoon.microdustapp.data.model.region


import com.google.gson.annotations.SerializedName

data class RegionInfo(
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("parentId")
    val parentId: String,
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("regionId")
    val regionId: String,
    @SerializedName("regionName")
    val regionName: String
)