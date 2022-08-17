package com.hoon.microdustapp.data.util.constants

enum class AirPollution(val pollutionName: String, val maximumValue: Double) {
    PM_10("미세먼지", 76.0),
    PM_2_5("초미세먼지", 151.0),
    CAI("통합환경지수", 251.0),
    O3("오존", 0.151),
    NO2("이산화질소", 0.201),
    CO("일산화탄소", 15.01),
    SO2("아황산가스", 0.151)
}