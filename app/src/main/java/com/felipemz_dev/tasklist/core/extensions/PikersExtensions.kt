package com.felipemz_dev.tasklist.core.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    this.time = format.parse(time)!!
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

fun String.reduce(): String {
    return if (this.length > 50) {
        this.substring(0, 50) + "..."
    } else {
        this
    }
}