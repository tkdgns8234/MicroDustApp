package com.hoon.microdustapp.data.repository

import com.hoon.microdustapp.data.api.AirKoreaApiService
import com.hoon.microdustapp.data.mapper.toModel
import com.hoon.microdustapp.data.model.ForecastModel
import com.hoon.microdustapp.data.response.measure.MeasureResult
import com.hoon.microdustapp.data.response.measuringstation.StationInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AirPollutionRepositoryImpl(
    private val airKoreaApiService: AirKoreaApiService,
    private val addressRepository: AddressRepository,
    private val ioDispatcher: CoroutineDispatcher
) : AirPollutionRepository {

    override suspend fun getNearbyMeasuringStation(
        latitude: Double,
        longitude: Double
    ): StationInfo? = withContext(ioDispatcher) {
        val tmCoordinate = addressRepository.getTmCoordinate(latitude,longitude)
        tmCoordinate ?: return@withContext null

        airKoreaApiService.searchNearbyMeasuringStation(tmCoordinate.first, tmCoordinate.second)
            .body()
            ?.response
            ?.body
            ?.stationInfos
            ?.firstOrNull()
    }

    override suspend fun getMeasureInfo(stationName: String): MeasureResult? =
        withContext(ioDispatcher) {
            val results = airKoreaApiService.getMeasureInfo(stationName)
                .body()
                ?.response
                ?.body
                ?.MeasureResults

            results?.forEach {
                // 필수 정보인 미세먼지, 초미세먼지 정보가 정상적으로 존재하는 경우 해당 데이터 return
                if (it.pm10Grade != null &&
                    it.pm25Grade != null &&
                    it.pm10Value.toIntOrNull() != null &&
                    it.pm25Value.toIntOrNull() != null
                ) {
                    return@withContext it
                }
            }
            return@withContext null
        }

    override suspend fun getForecastInfo(searchDate: String): List<ForecastModel>? =
        withContext(ioDispatcher) {
            airKoreaApiService.getForecastInfo(searchDate)
                .body()
                ?.response
                ?.body
                ?.forecastItems
                ?.map {
                    it.toModel()
                }
        }
}