package com.felipemz_dev.tasklist.core.extensions

import android.app.Activity
import android.app.AlertDialog
import android.widget.EditText
import com.felipemz_dev.tasklist.R

fun Activity.showInputDialog(
    text: String = "",
    positiveButtonListener: (String) -> Unit
) {
    val builder = AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.dialog_task_step, null)

    val editText = dialogView.findViewById<EditText>(R.id.inputEditText)
    editText.setText(text)

    builder.setView(dialogView)
        .setTitle(R.string.new_step)
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
    text: String,
    positiveButtonListener: (Boolean) -> Unit
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(R.string.dialog_confirm_delete)
        .setMessage(text)
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