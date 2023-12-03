package com.felipemz_dev.tasklist.core.extensions

import android.app.Activity
import android.content.res.ColorStateList
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textfield.TextInputLayout

fun RecyclerView.onSwipeItem(function: (viewHolder: ViewHolder) -> Unit) {
    val directions = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, directions) {
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
    return if (index == this.size - 1) 0 else index + 1
}

fun Activity.makeResourcesToast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    val text = resources.getString(resId)
    Toast.makeText(this, text, duration).show()
}

fun TextInputLayout.addHintChangeStyle(color: Int) {
    if (isExpandedHintEnabled) hintTextColor =
        ColorStateList(arrayOf(intArrayOf(color)), intArrayOf(color))
}
