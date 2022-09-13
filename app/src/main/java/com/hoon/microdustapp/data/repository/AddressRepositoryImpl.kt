package com.hoon.microdustapp.data.repository

import android.util.Log
import com.hoon.microdustapp.data.api.KaKaoLocalApiService
import com.hoon.microdustapp.data.database.AddressDao
import com.hoon.microdustapp.data.database.AddressEntity
import com.hoon.microdustapp.data.mapper.toAddressModel
import com.hoon.microdustapp.data.mapper.toEntity
import com.hoon.microdustapp.data.mapper.toModel
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.response.address.Document
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class AddressRepositoryImpl(
    private val kakaoLocalApiService: KaKaoLocalApiService,
    private val addressDao: AddressDao,
    private val ioDispatcher: CoroutineDispatcher
) : AddressRepository {

    override val addressFlow: Flow<List<AddressModel>> =
        addressDao.getAll()
            .distinctUntilChanged() // 변경사항이 생기지 않으면 수집되지 않음
            .map { it.toModel() }
            .flowOn(ioDispatcher)

    override suspend fun getAddressFromKeyword(keyword: String): List<AddressModel>? =
        withContext(ioDispatcher) {
            kakaoLocalApiService.getAddressFromKeyword(keyword)
                .body()
                ?.documents
                ?.map {
                    it.toAddressModel()
                }
        }

    /**
     * 위 경도 값을 기반으로 TM 좌표를 가져옴
     */
    override suspend fun getTmCoordinate(
        latitude: Double,
        longitude: Double
    ): Pair<Double, Double>? =
        withContext(ioDispatcher) {
            val response = kakaoLocalApiService.getTmCoordinate(latitude, longitude)
            if (response.isSuccessful.not()) return@withContext null

            val tmx = response.body()!!.documents.first().x
            val tmy = response.body()!!.documents.first().y

            Log.e("Test", "tmx= " + tmx.toString() + "tmy =" + tmy.toString())

            return@withContext Pair(tmx, tmy)
        }

    override suspend fun insertAddressDB(addressModel: AddressModel) = withContext(ioDispatcher) {
        addressDao.insert(addressModel.toEntity())
    }

    override suspend fun findAddressDB(addressName: String): AddressModel? =
        withContext(ioDispatcher) {
            addressDao.find(addressName)?.toModel()
        }

    override suspend fun deleteAddressDB(addressModel: AddressModel) {
        addressDao.delete(addressModel.toEntity())
    }

}