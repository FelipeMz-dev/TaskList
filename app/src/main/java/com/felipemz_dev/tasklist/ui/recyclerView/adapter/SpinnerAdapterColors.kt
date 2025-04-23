package com.felipemz_dev.tasklist.ui.recyclerView.adapter

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.felipemz_dev.tasklist.R

class SpinnerAdapterColors(context: Context): SpinnerAdapter {

    private val listColors = listOf(
        context.getColor(R.color.surfie_green) to context.getString(R.string.color_default),
        context.getColor(R.color.red) to context.getString(R.string.color_red),
        context.getColor(R.color.green) to context.getString(R.string.color_green),
        context.getColor(R.color.blue) to context.getString(R.string.color_blue),
        context.getColor(R.color.yellow) to context.getString(R.string.color_yellow),
        context.getColor(R.color.purple) to context.getString(R.string.color_purple),
        context.getColor(R.color.orange) to context.getString(R.string.color_oranfe),
        context.getColor(R.color.pink) to context.getString(R.string.color_pink),
        context.getColor(R.color.cyan) to context.getString(R.string.color_cyan),
    )

    override fun registerDataSetObserver(observer: DataSetObserver?) {}

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {}

    override fun getCount(): Int {
        return listColors.size
    }

    override fun getItem(position: Int): Any {
        return listColors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_spinner_color, parent, false)

        val colorView = view.findViewById<View>(R.id.colorView)
        val colorName = view.findViewById<TextView>(R.id.colorName)

        val colorText = if (ColorUtils.calculateLuminance(listColors[position].first) < 0.5) {
            Color.WHITE
        } else {
            Color.BLACK
        }

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(listColors[position].first)
        }
        colorView.background = drawable
        colorName.text = listColors[position].second
        colorName.setTextColor(colorText)

        return view
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return listColors.isEmpty()
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_spinner_color, parent, false)

        val colorView = view.findViewById<View>(R.id.colorView)
        val colorName = view.findViewById<TextView>(R.id.colorName)

        val colorText = if (ColorUtils.calculateLuminance(listColors[position].first) < 0.5) {
            Color.WHITE
        } else {
            Color.BLACK
        }

        colorView.setBackgroundColor(listColors[position].first)
        colorName.text = listColors[position].second
        colorName.setTextColor(colorText)

        return view
    }
}