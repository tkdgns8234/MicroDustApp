package com.hoon.microdustapp.data.model

/**
 * @param imageUrlMicroDust 미세먼지 gif
 * @param imageUrlUltraMicroDust 초미세먼지 gif
 * @param updateTime 예보 시간
 * @param contents 예보 내용
 */

data class ForecastModel(
    val imageUrlMicroDust: String,
    val imageUrlUltraMicroDust: String,
    val updateTime: String,
    val contents: String
)