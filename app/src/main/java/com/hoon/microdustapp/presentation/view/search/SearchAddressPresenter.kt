package com.hoon.microdustapp.presentation.view.search

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.data.repository.AddressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchAddressPresenter(
    private val view: SearchAddressContract.View,
    private val addressRepository: AddressRepository
) : SearchAddressContract.Presenter {

    override val scope: CoroutineScope
        get() = MainScope()

    override fun onCreateView() {}

    override fun onDestroyView() {
        scope.cancel()
    }

    override fun addFavoriteAddress(addressModel: AddressModel) {
        scope.launch {
            val isExist = addressRepository.findAddressDB(addressModel.addressName) != null

            view.handleAddFavoriteAddressResult(isExist, addressModel)
        }
    }

    override fun getAddressListFromKeyword(query: String) {
        scope.launch {
            val result = addressRepository.getAddressFromKeyword(query)
            view.updateAddressList(result)
        }
    }

}