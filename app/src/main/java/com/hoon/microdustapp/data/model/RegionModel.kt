package com.hoon.microdustapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegionModel(
    val regionId: String,
    val description: String
) : Parcelable
