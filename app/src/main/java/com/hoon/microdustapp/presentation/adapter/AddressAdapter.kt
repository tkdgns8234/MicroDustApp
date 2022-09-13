package com.hoon.microdustapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.data.model.AddressModel
import com.hoon.microdustapp.databinding.ItemRegionBinding
import com.hoon.microdustapp.databinding.ItemRegionDrawableViewBinding
import com.hoon.microdustapp.presentation.adapter.holder.BaseAddressViewHolder
import com.hoon.microdustapp.presentation.adapter.holder.FavoriteViewHolder
import com.hoon.microdustapp.presentation.adapter.holder.SearchViewHolder
import java.util.*

class AddressAdapter<ViewHolder : BaseAddressViewHolder>(
    val holderType: HolderType,
    val onClick: (model: AddressModel) -> Unit
) : ListAdapter<AddressModel, ViewHolder>(diffutils) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return if (holderType == HolderType.TYPE_FAVORITE) {
            val binding = ItemRegionDrawableViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            FavoriteViewHolder(binding, onClick) as ViewHolder
        } else {
            val binding =
                ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SearchViewHolder(binding, onClick) as ViewHolder
        }
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

        enum class HolderType {
            TYPE_FAVORITE,
            TYPE_SEARCH
        }

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