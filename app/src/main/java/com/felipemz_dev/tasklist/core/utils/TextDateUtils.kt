package com.felipemz_dev.tasklist.core.utils

import android.content.Context
import com.felipemz_dev.tasklist.R
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

        fun isDateExpired(time: Long): Boolean {
            return time < Calendar.getInstance().timeInMillis - 12000
        }

        fun calculateTimeDifference(context: Context, time: String): String {
            val currentCalendar = Calendar.getInstance()
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = format.parse(time)!!
            val difference = calendar.timeInMillis - currentCalendar.timeInMillis

            //adverb text
            val adverbDifference = if (difference > 0) context.getString(R.string.dialog_before)
            else context.getString(R.string.dialog_after)

            val txtDays = context.getString(R.string.days)
            val txtHours = context.getString(R.string.hours)
            val txtMinutes = context.getString(R.string.minutes)

            val days = kotlin.math.abs((difference / (24 * 60 * 60 * 1000)).toInt())
            val hours = kotlin.math.abs(((difference % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)).toInt())
            val minutes = kotlin.math.abs(((difference % (60 * 60 * 1000)) / (60 * 1000)).toInt())

            return "${
                if (days > 0) {
                    if (days == 1) "$days ${
                        txtDays.replace("s", "")
                    }" 
                    else "$days $txtDays"
                } else ""
            } ${
                if (hours > 0) {
                    if (minutes == 1) "$hours $txtHours"
                    else "$hours $txtHours"
                } else ""
            } ${
                if (minutes > 0) {
                    if (minutes == 1) "$minutes $txtMinutes"
                    else "$minutes $txtMinutes"
                } else ""
            } ${
                if (days == 0 && hours == 0 && minutes == 0) context.getString(R.string.just_now)
                else adverbDifference
            }"
        }
    }
}