package com.hoon.microdustapp.presentation.adapter.holder

import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.databinding.ItemRegionDrawableViewBinding

class FavoriteViewHolder(
    private val binding: ItemRegionDrawableViewBinding,
    private val onClick: (model: AddressModel) -> Unit
) : BaseAddressViewHolder(binding) {

    override fun bind(model: AddressModel) = with(binding) {
        textViewRegion.text = model.addressName
        root.setOnClickListener {
            onClick(model)
        }
    }

    fun setAlpha(alpha: Float) {
        binding.textViewRegion.alpha = alpha
    }
}