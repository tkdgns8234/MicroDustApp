package com.hoon.microdustapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressModel(
    val addressName: String,
    val x: Double,
    val y: Double
) : Parcelable
