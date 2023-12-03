package com.felipemz_dev.tasklist.core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TextDateUtils {
    companion object {

        fun loadDateOnTextView(calendar: Calendar = Calendar.getInstance()): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }

        fun loadTimeOnTextView(
            calendar: Calendar = Calendar.getInstance().apply {
                set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE) + 1)
            }
        ): String {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return timeFormat.format(calendar.time)
        }

        fun fillExpiryDateTexts(text: String, isRemember: Boolean): Pair<String, String> =
            if (isRemember){
                val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.time = format.parse(text)!!
                Pair(loadDateOnTextView(calendar), loadTimeOnTextView(calendar))
            } else {
                Pair(loadDateOnTextView(), loadTimeOnTextView())
            }

        fun isDateExpired(dateText: String, timeText: String): Boolean {
            if (dateText.trim().isEmpty() || timeText.trim().isEmpty()) return false
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = format.parse("$dateText $timeText")!!
            return calendar.timeInMillis < Calendar.getInstance().timeInMillis - 12000
        }
    }
}