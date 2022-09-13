package com.hoon.microdustapp.presentation.adapter.callback

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hoon.microdustapp.presentation.adapter.AddressAdapter
import com.hoon.microdustapp.presentation.adapter.holder.FavoriteViewHolder

class ItemTouchHelperCallback(
    private val deleteItemCallback: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.RIGHT
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // onMove callback에서 어댑터의 위치를 계속 변환해야함
        // 따라서 adapterPosition 를 사용해 어댑터의 위치를 계속 추적해야한다.
        (recyclerView.adapter as AddressAdapter).moveItem(
            viewHolder.adapterPosition,
            target.adapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = (viewHolder as FavoriteViewHolder).adapterPosition
        deleteItemCallback(position)
    }

    // 뷰 홀더 클릭 중 호출되는 콜백
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.ACTION_STATE_SWIPE -> {
                (viewHolder as FavoriteViewHolder).setAlpha(0.5f)
            }
        }
    }

    // 뷰 홀더 클릭을 놔줬을 때 호출되는 콜백
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        (viewHolder as FavoriteViewHolder).setAlpha(1.0f)
    }
}