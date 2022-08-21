package com.hoon.microdustapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.data.model.RegionModel
import com.hoon.microdustapp.databinding.ItemRegionBinding

class SearchRegionAdapter(val onClick: (model: RegionModel) -> Unit) :
    ListAdapter<RegionModel, SearchRegionAdapter.ViewHolder>(diffutils) {

    inner class ViewHolder(private val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: RegionModel) = with(binding) {
            textViewRegion.text = model.description
            root.setOnClickListener {
                onClick(model)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRegionAdapter.ViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchRegionAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffutils = object : DiffUtil.ItemCallback<RegionModel>() {
            override fun areItemsTheSame(oldItem: RegionModel, newItem: RegionModel): Boolean {
                return oldItem.regionId == newItem.regionId
            }

            override fun areContentsTheSame(oldItem: RegionModel, newItem: RegionModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}