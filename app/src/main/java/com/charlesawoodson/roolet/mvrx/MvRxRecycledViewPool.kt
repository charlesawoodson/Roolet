package com.charlesawoodson.roolet.mvrx

/**
 * Created by charles.adams on 05/24/2020
 */

import androidx.recyclerview.widget.RecyclerView

class MvRxRecycledViewPool : RecyclerView.RecycledViewPool() {

    private val viewTypes = HashSet<Int>()

    override fun putRecycledView(scrap: RecyclerView.ViewHolder) {
        val count = getRecycledViewCount(scrap.itemViewType)
        super.putRecycledView(scrap)

        if (getRecycledViewCount(scrap.itemViewType) > count) {
            // Keep track of the item types in the pool
            viewTypes.add(scrap.itemViewType)
        } else {
            // If we fail to add the view holder to the pool then destroy it
            (scrap as? MvRxListViewHolder<*>)?.destroy()
        }
    }

    override fun setMaxRecycledViews(viewType: Int, max: Int) {
        while (getRecycledViewCount(viewType) > max) {
            (getRecycledView(viewType) as? MvRxListViewHolder<*>)?.destroy()
        }
        super.setMaxRecycledViews(viewType, max)
    }

    override fun clear() {
        viewTypes.forEach { viewType ->
            while (true) {
                val viewHolder = getRecycledView(viewType) ?: break
                (viewHolder as? MvRxListViewHolder<*>)?.destroy()
            }
        }
        super.clear()
    }
}