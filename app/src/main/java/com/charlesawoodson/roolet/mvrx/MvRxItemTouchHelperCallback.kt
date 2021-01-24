package com.charlesawoodson.roolet.mvrx

/**
 * Created by charles.adams on 05/24/2020
 */

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

open class MvRxItemTouchHelperCallback(
    private val adapter: MvRxTouchableListAdapter<*>
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean = adapter.longPressDragEnabled

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        viewHolder as MvRxListViewHolder<*>

        val dragFlags = if (viewHolder.moveEnabled) adapter.dragFlags else 0
        val swipeDismissFlags = if (viewHolder.swipeDismissEnabled) adapter.swipeDismissFlags else 0

        return makeMovementFlags(dragFlags, swipeDismissFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        target as MvRxListViewHolder<*>

        if (!target.moveEnabled) return false

        adapter.moveItem(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.removeItem(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        viewHolder?.also {
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> adapter.onDragStart(viewHolder)
                ItemTouchHelper.ACTION_STATE_SWIPE -> adapter.onSwipeStart(viewHolder)
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.onTouchComplete(viewHolder)
    }
}
