package com.hoon.microdustapp.data.mapper

import com.hoon.microdustapp.data.database.AddressEntity
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.model.ForecastModel
import com.hoon.microdustapp.data.response.address.Document
import com.hoon.microdustapp.data.response.forecast.ForecastItem

fun ForecastItem.toModel(): ForecastModel {
    return ForecastModel(
        imageUrlMicroDust = this.imageUrl7,
        imageUrlUltraMicroDust = this.imageUrl8,
        updateTime = this.dataTime,
        contents = this.informCause
    )
}

fun Document.toAddressModel() : AddressModel{
    return AddressModel(
        addressName,
        x.toDouble(),
        y.toDouble()
    )
}

fun AddressModel.toEntity(): AddressEntity {
    return AddressEntity(
        addressName = addressName,
        x = x,
        y = y
    )
}

fun AddressEntity.toModel(): AddressModel {
    return AddressModel(
        addressName = addressName,
        x = x,
        y = y
    )
}

fun List<AddressEntity>.toModel(): List<AddressModel> = map { it.toModel() }
