package com.hoon.microdustapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.databinding.ItemFavoriteAddressBinding
import java.util.*

class FavoriteAddressAdapter(
    val onClick: (model: AddressModel) -> Unit
) : ListAdapter<AddressModel, FavoriteAddressAdapter.ViewHolder>(diffutils) {

    inner class ViewHolder(private val binding: ItemFavoriteAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: AddressModel) = with(binding) {
            textViewRegion.text = model.addressName
            root.setOnClickListener {
                onClick(model)
            }
        }

        fun setAlpha(alpha: Float) {
            binding.textViewRegion.alpha = alpha
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFavoriteAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val newList = currentList.toMutableList()
        Collections.swap(newList, fromPosition, toPosition)
        submitList(newList)
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