package com.example.tasklist.ui.view

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.tasklist.R
import com.example.tasklist.core.EditTaskViewModelFactory
import com.example.tasklist.core.makeTextToast
import com.example.tasklist.ui.recyclerView.adapter.TaskStepAdapter
import com.example.tasklist.core.onSwipeItem
import com.example.tasklist.core.makeCustomDatePicker
import com.example.tasklist.core.makeCustomDialog
import com.example.tasklist.data.TaskRepository
import com.example.tasklist.data.local.TaskDataBase
import com.example.tasklist.ui.viewmodel.EditTaskViewModel
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var etTaskTextEdit: EditText
    private lateinit var tvExpiryDateEdit: TextView
    private lateinit var btnAddTaskStep: ImageButton
    private lateinit var btnCancelTaskEdit: Button
    private lateinit var btnApplyTaskEdit: Button
    private lateinit var taskStepAdapter: TaskStepAdapter
    private lateinit var rvTaskSteps: RecyclerView
    private var listSteps = mutableListOf<String>()

    private val viewModel: EditTaskViewModel by viewModels {
        EditTaskViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        initComponents()
        initUI()
        initExtraData()
    }

    private fun initComponents() {
        etTaskTextEdit = findViewById(R.id.etTaskTextEdit)
        tvExpiryDateEdit = findViewById(R.id.tvExpiryDateEdit)
        btnAddTaskStep = findViewById(R.id.btnAddTaskStep)
        btnApplyTaskEdit = findViewById(R.id.btnApplyTaskEdit)
        btnCancelTaskEdit = findViewById(R.id.btnCancelTaskEdit)
        rvTaskSteps = findViewById(R.id.rvTaskSteps)
    }

    private fun initUI() {
        tvExpiryDateEdit.setOnClickListener { showDatePicker() }
        btnAddTaskStep.setOnClickListener { showInputDialog() }
        btnCancelTaskEdit.setOnClickListener { finish() }
        btnApplyTaskEdit.setOnClickListener { saveData() }
        taskStepAdapter = TaskStepAdapter(listSteps) { position: Int, text: String ->
            showInputDialog(position, text)
        }
        rvTaskSteps.layoutManager = LinearLayoutManager(this)
        rvTaskSteps.adapter = taskStepAdapter
        rvTaskSteps.onSwipeItem { deleteItem(it) }
    }

    private fun initExtraData() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val taskId = bundle.getLong("id_task")
            viewModel.getItemData(taskId)
            renderTask()
            return
        }
        loadDateOnTextView()
    }

    private fun deleteItem(viewHolder: ViewHolder) {
        viewHolder as TaskStepAdapter.TaskStepsViewHolder
        val position = viewHolder.adapterPosition
        taskStepAdapter.deleteItem(position)
    }

    private fun saveData() {
        val arg = etTaskTextEdit.text.toString().ifEmpty {
            makeTextToast(R.string.empty_text)
            return
        }
        val expiryDate = tvExpiryDateEdit.text.toString()
        val listSteps = listSteps.joinToString("|")
        viewModel.saveData(arg, expiryDate, listSteps)
        finish()
    }

    private fun renderTask() {
        viewModel.task.observe(this) { task ->
            etTaskTextEdit.setText(task.taskText)
            tvExpiryDateEdit.text = task.expiryDate
            val stepsString = task.listSteps
            if (stepsString.isNotEmpty()) {
                listSteps.addAll(stepsString.split("|").toList())
                taskStepAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showInputDialog() {
        AlertDialog.Builder(this).makeCustomDialog(this.layoutInflater) {
            addStep(it)
        }.show()
    }

    private fun showInputDialog(position: Int, text: String) {
        AlertDialog.Builder(this).makeCustomDialog(this.layoutInflater, text) {
            editStep(it, position)
        }.show()
    }

    private fun addStep(text: String) {
        if (text.isNotEmpty()) {
            listSteps.add(text)
            taskStepAdapter.notifyItemInserted(listSteps.size)
        }
    }

    private fun editStep(text: String, position: Int) {
        if (text.isNotEmpty()) {
            listSteps[position - 1] = text
            taskStepAdapter.notifyItemChanged(position - 1)
        }
    }

    private fun showDatePicker() {
        Calendar.getInstance().makeCustomDatePicker(this){
            loadDateOnTextView(it)
        }.show()
    }

    private fun loadDateOnTextView(calendar: Calendar = Calendar.getInstance()) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        tvExpiryDateEdit.text = formattedDate
    }
}