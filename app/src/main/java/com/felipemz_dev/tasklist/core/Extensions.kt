package com.felipemz_dev.tasklist.core

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.felipemz_dev.tasklist.R
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

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
    date: String,
    onSelected: (Calendar) -> Unit
): DatePickerDialog {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    this.time = format.parse(date)
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

fun Calendar.makeCustomTimePicker(
    context: Context,
    time: String,
    onSelected: (Calendar) -> Unit
): TimePickerDialog {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    this.time = format.parse(time)
    val hour = this.get(Calendar.HOUR_OF_DAY)
    val minute = this.get(Calendar.MINUTE)

    val timePicker = TimePickerDialog(context, { _, hourSelected, minuteSelected ->
        val selectedTime = Calendar.getInstance()
        selectedTime.set(Calendar.HOUR_OF_DAY, hourSelected)
        selectedTime.set(Calendar.MINUTE, minuteSelected)
        onSelected(selectedTime)
    }, hour, minute, false)

    return timePicker
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
    val imm = inflater.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    builder.setTitle(R.string.new_step)
    builder.setPositiveButton(R.string.accept) { _, _ ->
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        val userInputText = inputEditText.text.toString()
        onAccept(userInputText)
    }
    builder.setNegativeButton(R.string.cancel) { dialog, _ ->
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        dialog.dismiss()
    }
    return builder.create()
}

fun String.reduce(): String {
    return if (this.length > 50) {
        this.substring(0, 50) + "..."
    } else {
        this
    }
}

fun TextInputLayout.addHintChangeStyle(color : Int) {
    if (isExpandedHintEnabled) hintTextColor = ColorStateList(arrayOf(intArrayOf(color)), intArrayOf(color))
}