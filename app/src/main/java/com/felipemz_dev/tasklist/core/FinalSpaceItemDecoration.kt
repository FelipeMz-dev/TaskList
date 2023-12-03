package com.felipemz_dev.tasklist.core

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class FinalSpaceItemDecoration(private val finalSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = finalSpace
        }
    }
}