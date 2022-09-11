package com.hoon.microdustapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoon.microdustapp.databinding.ItemForecastVideoViewpagerBinding

class ForecastVideoAdapter : ListAdapter<String, ForecastVideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemForecastVideoViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide
                .with(binding.root.context)
                .load(url)
                .fitCenter()
                .into(binding.imageViewForecastVideo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemForecastVideoViewpagerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }
}