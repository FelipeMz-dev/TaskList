package com.example.tasklist

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var etTaskTextEdit: EditText
    private lateinit var tvExpiryDateEdit: TextView
    private lateinit var btnAddTaskStep: ImageButton
    private lateinit var btnDeleteTaskEdit: Button
    private lateinit var btnApplyTaskEdit: Button
    private lateinit var taskStepAdapter: TaskStepAdapter
    private lateinit var rvTaskSteps: RecyclerView
    private var listSteps = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        initComponents()
        initUI()
    }

    private fun initComponents(){
        etTaskTextEdit = findViewById(R.id.etTaskTextEdit)
        tvExpiryDateEdit = findViewById(R.id.tvExpiryDateEdit)
        btnAddTaskStep = findViewById(R.id.btnAddTaskStep)
        btnApplyTaskEdit = findViewById(R.id.btnApplyTaskEdit)
        btnDeleteTaskEdit = findViewById(R.id.btnDeleteTaskEdit)
        rvTaskSteps = findViewById(R.id.rvTaskSteps)
    }

    private fun initUI(){
        tvExpiryDateEdit.setOnClickListener {
            showDatePicker()
        }
        //Current Date
        updateDateOnTextView(Calendar.getInstance())
        btnAddTaskStep.setOnClickListener { showInputDialog() }
        btnApplyTaskEdit.setOnClickListener { saveNewTask() }
        btnDeleteTaskEdit.setOnClickListener { deleteTask() }
        //List of steps
        taskStepAdapter = TaskStepAdapter(listSteps)
        rvTaskSteps.layoutManager = LinearLayoutManager(this)
        rvTaskSteps.adapter = taskStepAdapter
    }

    private fun saveNewTask(){
        //TODO: save task
    }

    private fun deleteTask(){
        finish()
        //TODO: delete task
    }

    private fun showInputDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_task_step, null)
        dialogBuilder.setView(dialogView)

        val inputEditText: EditText = dialogView.findViewById(R.id.inputEditText)

        dialogBuilder.setTitle(R.string.new_step)
        dialogBuilder.setPositiveButton(R.string.add) { _, _ ->
            val userInputText = inputEditText.text.toString()
            addStep(userInputText)
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun addStep(text: String){
        //TODO: add step to task
    }

    private fun showDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, yearSelected, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
            updateDateOnTextView(selectedDate)
        }, year, month, day)

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun updateDateOnTextView(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tvExpiryDateEdit.text = formattedDate
    }
}