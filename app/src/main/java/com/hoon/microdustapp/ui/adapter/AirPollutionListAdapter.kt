package com.hoon.microdustapp.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.data.model.AirPollutionModel
import com.hoon.microdustapp.databinding.ItemAirPollutionBinding

class AirPollutionListAdapter(val onClick: (index: Int) -> Any) :
    ListAdapter<AirPollutionModel, AirPollutionListAdapter.ViewHolder>(diffutils) {

    inner class ViewHolder(private val binding: ItemAirPollutionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: AirPollutionModel) = with(binding) {
            tvTitle.text = model.name
            tvGrade.text = model.grade.gradeText
            tvValue.text = model.value

            // 0 이하의 수치들을 progress로 표시하기위해 1000을 곱한값을 설정
            val doubleVal = (model.value.toDoubleOrNull() ?: 0.0) // model.value 값이 숫자가 아닌 경우가 있음

            // max값을 먼저 지정해야함 (sdk 코드 보면 progress 적용 시 값을 max 이상 min 이하로 지정할 수 없음)
            progressbar.max = (model.maxValue * 1000).toInt()
            progressbar.progress = (doubleVal * 1000).toInt()
            progressbar.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, model.grade.colorID))

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AirPollutionListAdapter.ViewHolder {
        val binding =
            ItemAirPollutionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AirPollutionListAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffutils = object : DiffUtil.ItemCallback<AirPollutionModel>() {
            override fun areItemsTheSame(
                oldItem: AirPollutionModel,
                newItem: AirPollutionModel
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: AirPollutionModel,
                newItem: AirPollutionModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}