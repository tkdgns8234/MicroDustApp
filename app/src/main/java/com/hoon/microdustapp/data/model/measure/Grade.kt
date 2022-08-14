package com.hoon.microdustapp.data.model.measure

import com.google.gson.annotations.SerializedName
import com.hoon.microdustapp.R

enum class Grade(
    val text: String,
    val ResColorID: Int
) {
    @SerializedName("1")
    GOOD("좋음", R.color.material_right_blue),

    @SerializedName("2")
    NORMAL("보통", R.color.material_right_green),

    @SerializedName("3")
    BAD("나쁨", R.color.material_yellow),

    @SerializedName("4")
    VERYBAD("매우 나쁨", R.color.material_red);

    override fun toString(): String {
        return text
    }
}