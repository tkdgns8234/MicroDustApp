package com.hoon.microdustapp.data.repository

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.response.address.Document
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    val addressFlow: Flow<List<AddressModel>>

    suspend fun getAddressFromKeyword(keyword: String): List<AddressModel>?

    // 위, 경도값을 기반으로 TM 좌표를 return
    suspend fun getTmCoordinate(latitude: Double, longitude: Double): Pair<Double, Double>?

    suspend fun insertAddressDB(addressModel: AddressModel)

    suspend fun deleteAddressDB(addressModel: AddressModel)

    suspend fun findAddressDB(addressName: String): AddressModel?
}