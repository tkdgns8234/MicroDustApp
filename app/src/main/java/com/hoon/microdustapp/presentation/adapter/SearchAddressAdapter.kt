package com.hoon.microdustapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.databinding.ItemRegionBinding

class SearchAddressAdapter(val onClick: (model: AddressModel) -> Unit) :
    ListAdapter<AddressModel, SearchAddressAdapter.ViewHolder>(diffutils) {

    inner class ViewHolder(private val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: AddressModel) = with(binding) {
            textViewRegion.text = model.addressName
            root.setOnClickListener {
                onClick(model)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffutils = object : DiffUtil.ItemCallback<AddressModel>() {
            override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
                return oldItem.addressName == newItem.addressName
            }

            override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}