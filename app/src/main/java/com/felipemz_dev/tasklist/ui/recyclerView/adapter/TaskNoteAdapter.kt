package com.felipemz_dev.tasklist.ui.recyclerView.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.felipemz_dev.tasklist.core.utils.ImageUtils
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.ui.view.EditTaskActivity

class TaskNoteAdapter(
    step: LiveData<List<String>>,
    private val onItemClickListener: (Int, String) -> Unit
) : RecyclerView.Adapter<TaskNoteAdapter.TaskStepsViewHolder>() {

    private var notes: List<String> = emptyList()

    init {
        step.observeForever {
            notes = it?.toList() ?: emptyList()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskStepsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_step_to_task, parent, false)
        return TaskStepsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskStepsViewHolder, position: Int) {
        holder.bindData(position)
    }

    //fun deleteItem(position: Int) {
    //    notes.removeAt(position)
    //    notifyDataSetChanged()
    //}

    inner class TaskStepsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvNote: TextView = view.findViewById(R.id.tvNote)
        private val tvNumberNote: TextView = view.findViewById(R.id.tvNumberNote)
        private val ivNoteImage: ImageView = view.findViewById(R.id.ivNoteImage)

        fun bindData(position: Int) {
            val note = notes[position]
            tvNumberNote.text = "${position +1}."
            itemView.setOnClickListener { onItemClickListener(position, note) }

            ivNoteImage.visibility = View.GONE
            tvNote.visibility = View.GONE

            if (note.contains(EditTaskActivity.TAG_IS_IMAGE_NOTE)) {
                val noteImage = note.replace(EditTaskActivity.TAG_IS_IMAGE_NOTE, "")
                if (ImageUtils.containsAbsolutePath(noteImage)) {
                    ivNoteImage.visibility = View.VISIBLE
                    val uri = Uri.parse(noteImage)
                    if (ImageUtils.isUriValid(uri)) {
                        ivNoteImage.setImageURI(uri)
                    } else {
                        ivNoteImage.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_launcher_foreground))
                    }
                    tvNote.visibility = View.GONE
                }else{
                    ivNoteImage.visibility = View.GONE
                    tvNote.visibility = View.VISIBLE
                    tvNote.text = note
                }
            }else {
                ivNoteImage.visibility = View.GONE
                tvNote.visibility = View.VISIBLE
                tvNote.text = note
            }
        }
    }
}