package com.hoon.microdustapp.data.model

import com.hoon.microdustapp.data.model.measure.Grade

data class AirPollutionModel(
    val title: String,
    val grade: Grade,
    val value: String
)