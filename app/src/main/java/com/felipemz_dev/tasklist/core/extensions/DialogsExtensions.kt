package com.felipemz_dev.tasklist.core.extensions

import android.app.Activity
import android.app.AlertDialog
import android.widget.EditText
import android.widget.TextView
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.utils.TextDateUtils.Companion.calculateTimeDifference

fun Activity.showInputDialog(
    text: String = "",
    positiveButtonListener: (String) -> Unit
) {
    val builder = AlertDialog.Builder(this)
    val dialogView = layoutInflater.inflate(R.layout.dialog_task_step, null)

    val editText = dialogView.findViewById<EditText>(R.id.inputEditText)
    editText.setText(text)

    builder.setView(dialogView)
        .setTitle(R.string.new_note)
        .setPositiveButton(R.string.accept) { _, _ ->
            val inputText = editText.text.toString()
            positiveButtonListener.invoke(inputText)
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

    val dialog = builder.create()

    dialog.show()
}

fun Activity.showDialogDelete(
    textMessage: String,
    isDone: Boolean = false,
    positiveButtonListener: (Boolean) -> Unit
) {
    val builder = AlertDialog.Builder(this)
    val dialogView = layoutInflater.inflate(R.layout.view_dialog, null)

    val editText = dialogView.findViewById<TextView>(R.id.tvContentDialog)
    editText.apply {
        if (isDone) text = context.getString(R.string.already_you_have_done)
        else {
            text = context.getString(R.string.not_you_have_done)
            setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                this@showDialogDelete.getDrawable(R.drawable.baseline_warning_24),
                null
            )
            setTextColor(this@showDialogDelete.resources.getColorStateList(R.color.wheat))
        }
    }

    builder.setView(dialogView)
    builder.setTitle(R.string.dialog_confirm_delete)
        .setMessage(textMessage)
        .setPositiveButton(R.string.accept) { _, _ ->
            positiveButtonListener.invoke(true)
        }
        .setNegativeButton(R.string.cancel) { _, _ ->
            positiveButtonListener.invoke(false)
        }
        .setOnDismissListener {
            positiveButtonListener.invoke(false)
        }
    val dialog = builder.create()
    dialog.show()
}

fun Activity.showDialogDoneTask(
    textMessage: String,
    dateText: String,
    positiveButtonListener: (Boolean) -> Unit
){
    val builder = AlertDialog.Builder(this)
    val dialogView = layoutInflater.inflate(R.layout.view_dialog, null)
    val editText = dialogView.findViewById<TextView>(R.id.tvContentDialog)
    val difference = calculateTimeDifference(this, dateText)
    editText.apply {
        text = difference
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            this@showDialogDoneTask.getDrawable(R.drawable.baseline_hourglass_empty_24),
            null
        )
    }
    builder.setTitle(R.string.dialog_check_done_task)
        .setView(dialogView)
        .setMessage(textMessage)
        .setPositiveButton(R.string.confirm) { _, _ ->
            positiveButtonListener.invoke(true)
        }
        .setNegativeButton(R.string.dialog_button_false_alarm) { _, _ ->
            positiveButtonListener.invoke(false)
        }
        .setOnDismissListener {
            positiveButtonListener.invoke(false)
        }
    val dialog = builder.create()
    dialog.show()
}

fun Activity.showDialogDoneTask(
    text: String,
    positiveButtonListener: (Boolean) -> Unit
){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(R.string.dialog_check_done_task)
        .setMessage(text)
        .setPositiveButton(R.string.confirm) { _, _ ->
            positiveButtonListener.invoke(true)
        }
        .setNegativeButton(R.string.dialog_button_false_alarm) { _, _ ->
            positiveButtonListener.invoke(false)
        }
        .setOnDismissListener {
            positiveButtonListener.invoke(false)
        }
    val dialog = builder.create()
    dialog.show()
}