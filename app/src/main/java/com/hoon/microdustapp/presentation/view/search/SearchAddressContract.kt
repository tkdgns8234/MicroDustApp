package com.hoon.microdustapp.presentation.view.search

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.presentation.BasePresenter
import com.hoon.microdustapp.presentation.BaseView

interface SearchAddressContract {
    interface View: BaseView<Presenter> {
        fun showLoadingProgress()

        fun hideLoadingProgress()

        fun handleAddFavoriteAddressResult(addressModel: AddressModel?)

        fun updateAddressList(adressList: List<AddressModel>?)
    }

    interface Presenter: BasePresenter {
        fun addFavoriteAddress(addressName: String)

        fun getAddressListFromKeyword(query: String)
    }
}