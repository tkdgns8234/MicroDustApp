package com.hoon.microdustapp.presentation.view.main

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.repository.AddressRepository
import com.hoon.microdustapp.data.repository.AirPollutionRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class MainPresenter(
    private val view: MainContract.View,
    private val airPollutionRepository: AirPollutionRepository,
    private val addressRepository: AddressRepository
) : MainContract.Presenter {

    override val scope: CoroutineScope
        get() = MainScope()

    override fun onCreateView() {
        observeFavoriteAddressFlow()
    }

    override fun onDestroyView() {
        scope.cancel()
    }

    private fun observeFavoriteAddressFlow() {
        addressRepository.addressFlow
            .onEach { addressList ->
                view.updateFavoriteAddress(addressList)
            }.launchIn(scope)
    }

    /**
     * @param latitude: 위도, y
     * @param longitude: 경도, x
     */
    override fun updateAirPollutionInfo(latitude: Double, longitude: Double) {
        scope.launch {
            val measureStationInfo =
                airPollutionRepository.getNearbyMeasuringStation(latitude, longitude)
            val measureResult =
                airPollutionRepository.getMeasureInfo(measureStationInfo?.stationName!!)
            view.showLoadingProgress()
            view.updateMainUI(measureResult!!, measureStationInfo)
            view.hideLoadingProgress()
        }
    }

    override fun updateForecastInfo(searchDate: String) {
        scope.launch {
            airPollutionRepository.getForecastInfo(searchDate)?.let { forecastItems ->
                view.updateForecastUI(forecastItems)
            }
        }
    }

    override fun insertAddressDB(addressModel: AddressModel) {
        scope.launch {
            addressRepository.insertAddressDB(addressModel)
        }
    }

    override fun deleteAddressDB(addressModel: AddressModel) {
        scope.launch {
            addressRepository.deleteAddressDB(addressModel)
        }
    }

}