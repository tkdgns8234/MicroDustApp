package com.hoon.microdustapp.data.model.region


import com.google.gson.annotations.SerializedName

data class SearchRegionsInfo(
    @SerializedName("regionInfo")
    val regionInfo: RegionInfo
)