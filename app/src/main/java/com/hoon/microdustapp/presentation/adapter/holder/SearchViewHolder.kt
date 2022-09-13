package com.hoon.microdustapp.presentation.adapter.holder

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.databinding.ItemRegionBinding

class SearchViewHolder(
    private val binding: ItemRegionBinding,
    val onClick: (model: AddressModel) -> Unit
) : BaseAddressViewHolder(binding) {

    override fun bind(model: AddressModel) = with(binding) {
        textViewRegion.text = model.addressName
        root.setOnClickListener {
            onClick(model)
        }
    }

}