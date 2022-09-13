package com.hoon.microdustapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hoon.microdustapp.databinding.ItemForecastVideoViewpagerBinding

import com.hoon.microdustapp.R
import com.hoon.microdustapp.extensions.toast

class ForecastVideoAdapter : ListAdapter<String, ForecastVideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemForecastVideoViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {

            Glide
                .with(binding.root.context)
                .asGif()
                .load(url)
                .error(R.drawable.ic_unknown_24)
                .listener(listener)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imageViewForecastVideo)
        }

        private val listener = object : RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>?,
                isFirstResource: Boolean
            ): Boolean {
                binding.root.context.applicationContext.toast("예보 영상 로드에 실패하였습니다.")
                return false // 리스너가 주어진 예외에 대한 대상 업데이트를 처리한 경우 true
            }

            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: Target<GifDrawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

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