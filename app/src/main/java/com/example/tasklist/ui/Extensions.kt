package com.example.tasklist.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun RecyclerView.onSwipeItem(function: (viewHolder: ViewHolder) -> Unit) {
    val directions = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, directions){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder,
            target: ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            function(viewHolder)
        }
    })
    itemTouchHelper.attachToRecyclerView(this)
}

fun Array<String>.getCircularIndex(index: Int): Int {
    return if (index == this.size-1) 0 else index +1
}