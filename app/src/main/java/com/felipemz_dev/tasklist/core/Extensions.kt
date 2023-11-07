package com.felipemz_dev.tasklist.core

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.felipemz_dev.tasklist.R

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

fun Activity.makeTextToast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    val text = resources.getString(resId)
    Toast.makeText(this, text, duration).show()
}

fun Calendar.makeCustomDatePicker(
    context: Context,
    onSelected: (Calendar) -> Unit
): DatePickerDialog {
    val year = this.get(Calendar.YEAR)
    val month = this.get(Calendar.MONTH)
    val day = this.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(context, { _, yearSelected, monthOfYear, dayOfMonth ->
        val selectedDate = Calendar.getInstance()
        selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
        onSelected(selectedDate)
    }, year, month, day)

    datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
    return datePicker
}

fun AlertDialog.Builder.makeCustomDialog(
    inflater: LayoutInflater,
    text: String = "",
    onAccept: (text: String) -> Unit
): AlertDialog {
    val builder = this
    val dialogView = inflater.inflate(R.layout.dialog_task_step, null)
    builder.setView(dialogView)
    val inputEditText: EditText = dialogView.findViewById(R.id.inputEditText)
    inputEditText.setText(text)
    inputEditText.requestFocus()
    builder.setTitle(R.string.new_step)
    builder.setPositiveButton(R.string.accept) { _, _ ->
        val userInputText = inputEditText.text.toString()
        onAccept(userInputText)
    }
    builder.setNegativeButton(R.string.cancel) { dialog, _ ->
        dialog.dismiss()
    }
    return builder.create()
}