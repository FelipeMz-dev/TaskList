package com.felipemz_dev.tasklist.ui.view

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.SpinnerAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.ConsentManager
import com.felipemz_dev.tasklist.core.utils.PreferencesUtils
import com.felipemz_dev.tasklist.core.extensions.addHintChangeStyle
import com.felipemz_dev.tasklist.core.extensions.makeResourcesToast
import com.felipemz_dev.tasklist.core.extensions.onSwipeItem
import com.felipemz_dev.tasklist.core.extensions.showInputDialog
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler
import com.felipemz_dev.tasklist.core.utils.TextDateUtils
import com.felipemz_dev.tasklist.core.viewmodelfactory.EditTaskViewModelFactory
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskDataBase
import com.felipemz_dev.tasklist.databinding.ActivityEditTaskBinding
import com.felipemz_dev.tasklist.ui.recyclerView.adapter.SpinnerAdapterColors
import com.felipemz_dev.tasklist.ui.recyclerView.adapter.TaskNoteAdapter
import com.felipemz_dev.tasklist.ui.viewmodel.EditTaskViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class EditTaskActivity : AppCompatActivity() {

    companion object {
        const val CODE_SELECT_IMAGE = 101
        const val TAG_DELIMITER = "-|-"
        const val TAG_IS_IMAGE_NOTE = "TL-H21.Image"
    }

    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var taskStepAdapter: TaskNoteAdapter
    private var interstitialAd: InterstitialAd? = null
    private var adsCounter = 0
    private lateinit var alarmHelper: NotificationScheduler
    private lateinit var consentManager: ConsentManager

    private val viewModel: EditTaskViewModel by viewModels {
        EditTaskViewModelFactory(TaskRepository(TaskDataBase.getInstance(this).taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        consentManager = ConsentManager(this)
        alarmHelper = NotificationScheduler(this)
        viewModel.setAlarmHelper(alarmHelper)
        initUI()
        initExtraData()
        initAds()
        if (PreferencesUtils.isUserInitEdit(this)) setShowNoteHelp(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CODE_SELECT_IMAGE) {
            val imageBitmap = if (data?.data != null) {
                MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            } else data?.extras?.get("data") as Bitmap
            if (imageBitmap == null) {
                makeResourcesToast(R.string.error_load_image)
                return
            }
            viewModel.setNewImageToNote(imageBitmap)
        }
    }

    private fun initUI() {
        val context = this
        binding.apply {
            spinnerColors.adapter = SpinnerAdapterColors(context)
            tvExpiryDateEdit.setOnClickListener { viewModel.showDatePicker(context) }
            tvExpiryTimeEdit.setOnClickListener { viewModel.showTimePicker(context) }
            btnCancelTaskEdit.setOnClickListener { finish() }
            taskStepAdapter = TaskNoteAdapter(viewModel.listNote) { position, text ->
                editDataNote(position, text)
            }
            btnApplyTaskEdit.setOnClickListener {
                val isSaved = viewModel.saveData(
                    text = etTaskTextEdit.text.toString(),
                    color = spinnerColors.selectedItemPosition
                )
                if (isSaved) finish()
                else makeResourcesToast(R.string.empty_text)
            }
            btnAddTaskNote.setOnClickListener {
                showInputDialog {
                    viewModel.addNote(it)
                    checkAdsCounter()
                }
            }
            btnAddTaskImage.setOnClickListener {
                viewModel.openImageChooserIntent(context)
            }
            viewInfo.setOnClickListener { setShowNoteHelp(true) }
            btnApplyTakEdit.setOnClickListener { setShowNoteHelp(false) }
            rvTaskNotes.layoutManager = LinearLayoutManager(context)
            rvTaskNotes.adapter = taskStepAdapter
            rvTaskNotes.onSwipeItem {
                viewModel.removeNote(it as TaskNoteAdapter.TaskStepsViewHolder)
            }
            checkboxRemember.setOnCheckedChangeListener { _, isChecked ->
                viewModel.remember.value = isChecked
            }
            tlTaskTextEdit.addHintChangeStyle(
                resources.getColor(R.color.surfie_green)
            )
            viewModel.remember.observe(context) {
                checkboxRemember.isChecked = it
                tvExpiryDateEdit.visibility = if (it) View.VISIBLE else View.GONE
                tvExpiryTimeEdit.visibility = if (it) View.VISIBLE else View.GONE
            }
            viewModel.dateText.observe(context) {
                tvExpiryDateEdit.text = it
                checkExpiryDateReport()
            }
            viewModel.timeText.observe(context) {
                tvExpiryTimeEdit.text = it
                checkExpiryDateReport()
            }
        }
    }

    private fun setShowNoteHelp(isShow: Boolean) {
        val visible = if (isShow) View.VISIBLE else View.GONE
        val alpha = if (isShow) 0.3f else 1f
        binding.apply {
            viewNoteHelp.visibility = visible
            llButtonsEditTask.alpha = alpha
            llContentEditTask.alpha = alpha
        }
    }

    private fun editDataNote(position: Int, text: String) {
        if (text.contains(TAG_IS_IMAGE_NOTE)) {
            viewModel.openImageChooserIntent(this, position)
        } else {
            showInputDialog(text) {
                viewModel.editNote(it, position)
            }
        }
    }

    private fun checkExpiryDateReport() {
        binding.tvExpiredReport.apply {
            visibility = if (TextDateUtils.isDateExpired(
                    viewModel.dateText.value!!,
                    viewModel.timeText.value!!
                )
            ) View.VISIBLE
            else View.GONE
        }
    }

    private fun initExtraData() {
        val bundle = intent.extras
        if (bundle != null) {
            val taskId = bundle.getLong("id_task")
            viewModel.getItemData(taskId)
            renderTask()
        } else {
            viewModel.dateText.value = TextDateUtils.loadDateOnTextView()
            viewModel.timeText.value = TextDateUtils.loadTimeOnTextView()
        }
    }

    private fun renderTask() {
        viewModel.task.observe(this) { task ->
            binding.etTaskTextEdit.setText(task.taskText)
            binding.spinnerColors.setSelection(task.color)
            TextDateUtils.fillExpiryDateTexts(task.expiryDate, task.isRemember).let {
                viewModel.dateText.value = it.first
                viewModel.timeText.value = it.second
            }
        }
    }

    //                                                                          <- ads
    private fun initAds() {
        consentManager.checkConsentAndLoadAds {
            InterstitialAd.load(
                this,
                "ca-app-pub-7090484402312159/5429182422",
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        interstitialAd = null
                    }
                }
            )
        }
    }

    private fun checkAdsCounter() {
        if (adsCounter == 2) {
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