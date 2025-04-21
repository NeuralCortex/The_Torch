package com.nc.torch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.graphics.toColorInt
import com.nc.torch.MainActivity
import com.nc.torch.R

class ColorSpinnerAdapter(
    context: Context,
    private val colors: List<MainActivity.ColorItem>
) : ArrayAdapter<MainActivity.ColorItem>(context, 0, colors), SpinnerAdapter {

    private val inflater = LayoutInflater.from(context)

    override fun getView(pos: Int, v: View?, parent: ViewGroup): View {
        val view = v ?: inflater.inflate(R.layout.spinner_item_color, parent, false)
        val color = view.findViewById<View>(R.id.color)

        val item = colors[pos]
        color.setBackgroundColor(item.color.toColorInt())

        return view
    }

    override fun getDropDownView(pos: Int, v: View?, parent: ViewGroup): View {
        val view = v ?: inflater.inflate(R.layout.spinner_item_color, parent, false)
        val color = view.findViewById<View>(R.id.color)

        val item = colors[pos]
        color.setBackgroundColor(item.color.toColorInt())

        return view
    }
}