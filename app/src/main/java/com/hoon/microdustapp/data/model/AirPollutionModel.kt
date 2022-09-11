package com.hoon.microdustapp.data.model

import com.hoon.microdustapp.data.response.measure.Grade

data class AirPollutionModel(
    val name: String, // 이름 e.g) CO2
    val grade: Grade, // 등급 (좋음, 양호, 나쁨, 매우나쁨)
    val value: String, // 수치
    val maxValue: Double, // 최대 수치
)