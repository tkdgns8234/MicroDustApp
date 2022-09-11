package com.hoon.microdustapp.presentation.view.search

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

    override fun onDestroyView() { scope.cancel() }

    override fun addFavoriteAddress(addressName: String) {
        scope.launch {
            val model = addressRepository.findAddressDB(addressName)
            view.handleAddFavoriteAddressResult(model)
        }
    }

    override fun getAddressListFromKeyword(query: String) {
        scope.launch {
            val result = addressRepository.getAddressFromKeyword(query)
            view.updateAddressList(result)
        }
    }

}