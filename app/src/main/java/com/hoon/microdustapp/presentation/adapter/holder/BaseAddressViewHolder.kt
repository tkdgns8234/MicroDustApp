package com.hoon.microdustapp.presentation.adapter.holder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hoon.microdustapp.data.model.AddressModel

abstract class BaseAddressViewHolder(
    binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(addressModel: AddressModel)

}