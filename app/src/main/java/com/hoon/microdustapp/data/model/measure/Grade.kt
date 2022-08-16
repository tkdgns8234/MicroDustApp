package com.hoon.microdustapp.data.model.measure

import com.google.gson.annotations.SerializedName
import com.hoon.microdustapp.R

enum class Grade(
    val gradeText: String,
    val colorID: Int,
    val emojiDrawableID: Int,
    val descStringID: Int
) {
    @SerializedName("1")
    GOOD("좋음", R.color.good, R.drawable.good, R.string.desc_good),

    @SerializedName("2")
    NORMAL("보통", R.color.normal, R.drawable.normal, R.string.desc_normal),

    @SerializedName("3")
    BAD("나쁨", R.color.bad, R.drawable.bad, R.string.desc_bad),

    @SerializedName("4")
    VERYBAD("매우 나쁨", R.color.worst, R.drawable.worst, R.string.desc_worst),

    UNKNOWN("알 수 없음", R.color.gray, R.drawable.ic_unknown_24, R.string.desc_unknown)
}