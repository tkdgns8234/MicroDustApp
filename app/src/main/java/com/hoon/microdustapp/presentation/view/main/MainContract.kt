package com.hoon.microdustapp.presentation.view.main

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.model.ForecastModel
import com.hoon.microdustapp.data.response.measure.MeasureResult
import com.hoon.microdustapp.data.response.measuringstation.StationInfo
import com.hoon.microdustapp.presentation.BasePresenter
import com.hoon.microdustapp.presentation.BaseView

interface MainContract {

    interface View : BaseView<Presenter> {
        fun showLoadingProgress()

        fun hideLoadingProgress()

        fun updateMainUI(measureResult: MeasureResult, stationInfo: StationInfo)

        fun updateForecastUI(forecastItems: List<ForecastModel>)

        fun updateFavoriteAddress(addressList: List<AddressModel>)
    }

    interface Presenter : BasePresenter {

        fun updateAirPollutionInfo(currentLatitude: Double, currentLongitude: Double)

        fun insertAddressDB(addressModel: AddressModel)

        fun deleteAddressDB(addressModel: AddressModel)
    }
}
