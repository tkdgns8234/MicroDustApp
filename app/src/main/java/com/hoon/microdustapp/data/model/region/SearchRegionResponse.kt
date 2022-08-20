package com.hoon.microdustapp.data.model.region


import com.google.gson.annotations.SerializedName

data class SearchRegionResponse(
    @SerializedName("searchRegionsInfo")
    val searchRegionsInfo: List<SearchRegionsInfo>
)