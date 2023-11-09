package com.felipemz_dev.tasklist.ui.view

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.AlarmHelper
import com.felipemz_dev.tasklist.core.EditTaskViewModelFactory
import com.felipemz_dev.tasklist.core.addHintChangeStyle
import com.felipemz_dev.tasklist.core.makeTextToast
import com.felipemz_dev.tasklist.ui.recyclerView.adapter.TaskStepAdapter
import com.felipemz_dev.tasklist.core.onSwipeItem
import com.felipemz_dev.tasklist.core.makeCustomDatePicker
import com.felipemz_dev.tasklist.core.makeCustomDialog
import com.felipemz_dev.tasklist.core.makeCustomTimePicker
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskDataBase
import com.felipemz_dev.tasklist.databinding.ActivityEditTaskBinding
import com.felipemz_dev.tasklist.ui.viewmodel.EditTaskViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var taskStepAdapter: TaskStepAdapter
    private var interstitialAd: InterstitialAd? = null
    private var adsCounter = 0
    private var listSteps = mutableListOf<String>()
    private lateinit var alarmHelper: AlarmHelper

    private val viewModel: EditTaskViewModel by viewModels {
        EditTaskViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alarmHelper = AlarmHelper(this)
        viewModel.setAlarmHelper(alarmHelper)
        initUI()
        initExtraData()
        initAds()
        initRememberState()
    }

    private fun initRememberState() {
        viewModel.remember.observe(this){
            binding.tvExpiryDateEdit.visibility = if (it) View.VISIBLE else View.GONE
            binding.tvExpiryTimeEdit.visibility = if (it) View.VISIBLE else View.GONE
        }
        binding.checkboxRemember.setOnClickListener{
            it as CheckBox
            viewModel.remember.value = it.isChecked
        }
    }

    private fun initUI() {
        taskStepAdapter = TaskStepAdapter(listSteps, ::showInputDialog)
        binding.tvExpiryDateEdit.setOnClickListener { showDatePicker() }
        binding.tvExpiryTimeEdit.setOnClickListener { showTimePicker() }
        binding.btnAddTaskStep.setOnClickListener { showInputDialog() }
        binding.btnCancelTaskEdit.setOnClickListener { finish() }
        binding.btnApplyTaskEdit.setOnClickListener { saveData() }
        binding.rvTaskSteps.layoutManager = LinearLayoutManager(this)
        binding.rvTaskSteps.adapter = taskStepAdapter
        binding.rvTaskSteps.onSwipeItem { deleteItem(it) }
        binding.tlTaskTextEdit.addHintChangeStyle(
            resources.getColor(R.color.surfie_green)
        )
    }

    private fun initExtraData() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val taskId = bundle.getLong("id_task")
            viewModel.getItemData(taskId)
            renderTask()
        } else {
            loadDateOnTextView()
            val defaultTime = Calendar.getInstance()
            defaultTime.set(Calendar.HOUR_OF_DAY, 23)
            defaultTime.set(Calendar.MINUTE, 59)
            loadTimeOnTextView(defaultTime)
        }
    }

    private fun deleteItem(viewHolder: ViewHolder) {
        viewHolder as TaskStepAdapter.TaskStepsViewHolder
        val position = viewHolder.adapterPosition
        taskStepAdapter.deleteItem(position)
    }

    private fun saveData() {
        val arg = binding.etTaskTextEdit.text.toString().trim().ifEmpty {
            makeTextToast(R.string.empty_text)
            return
        }
        val expiryDate = if (viewModel.remember.value == true){
            binding.tvExpiryDateEdit.text.toString() + " " + binding.tvExpiryTimeEdit.text.toString()
        }else{ "" }
        val listSteps = listSteps.joinToString("|")
        viewModel.saveData(arg, expiryDate, listSteps)
        finish()
    }

    private fun renderTask() {
        viewModel.task.observe(this) { task ->
            binding.etTaskTextEdit.setText(task.taskText)
            fillExpiryDateTexts(task.expiryDate, task.isRemember)
            val stepsString = task.listSteps
            if (stepsString.isNotEmpty()) {
                listSteps.addAll(stepsString.split("|").toList())
                taskStepAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fillExpiryDateTexts(text: String, isRemember: Boolean) {
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        if (isRemember) calendar.time = format.parse(text)
        val textDate = dateFormat.format(calendar.time)
        val textTime = timeFormat.format(calendar.time)
        binding.checkboxRemember.isChecked = isRemember
        viewModel.remember.value = isRemember
        binding.tvExpiryDateEdit.text = textDate
        binding.tvExpiryTimeEdit.text = textTime
    }

    private fun showInputDialog() {
        AlertDialog.Builder(this).makeCustomDialog(this.layoutInflater) {
            checkAdsCounter()
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
        val currentDate = binding.tvExpiryDateEdit.text.toString()
        Calendar.getInstance().makeCustomDatePicker(
            this,
            currentDate
        ) {
            loadDateOnTextView(it)
        }.show()
    }

    private fun showTimePicker() {
        val currentTime = binding.tvExpiryTimeEdit.text.toString()
        Calendar.getInstance().makeCustomTimePicker(
            this,
            currentTime
        ) {
            loadTimeOnTextView(it)
        }.show()
    }

    private fun loadDateOnTextView(calendar: Calendar = Calendar.getInstance()) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        binding.tvExpiryDateEdit.text = formattedDate
    }

    private fun loadTimeOnTextView(calendar: Calendar) {
        val timeFormat = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)
        binding.tvExpiryTimeEdit.text = formattedTime
    }

    //                                                                          <- ads
    private fun initAds() {
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                }
            })
    }

    private fun checkAdsCounter() {
        if (adsCounter == 3) {
            adsCounter = 0
            showAds()
            initAds()
            return
        }
        adsCounter++
    }

    private fun showAds() {
        if (interstitialAd != null) {
            interstitialAd?.show(this)
            interstitialAd = null
        }
    }
}