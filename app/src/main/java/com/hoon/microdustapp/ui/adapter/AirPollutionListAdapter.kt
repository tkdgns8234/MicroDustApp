package com.hoon.microdustapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.data.model.AirPollutionModel
import com.hoon.microdustapp.databinding.ItemAirPollutionBinding

class AirPollutionListAdapter(val onClick: (index: Int) -> Any) : ListAdapter<AirPollutionModel, AirPollutionListAdapter.ViewHolder>(diffutils) {

    inner class ViewHolder(private val binding: ItemAirPollutionBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(model: AirPollutionModel) = with(binding) {
            tvTitle.text = model.title
            tvGrade.text = model.grade.gradeText
            tvValue.text = model.value
            progressbar.progress = model.value.toIntOrNull() ?: 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirPollutionListAdapter.ViewHolder {
        val binding = ItemAirPollutionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AirPollutionListAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffutils = object : DiffUtil.ItemCallback<AirPollutionModel>() {
            override fun areItemsTheSame(oldItem: AirPollutionModel, newItem: AirPollutionModel): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: AirPollutionModel, newItem: AirPollutionModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}