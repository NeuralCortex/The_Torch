package com.nc.torch.listener

import android.view.View

abstract class DoubleClickListener : View.OnClickListener {

    private var lastClickTime: Long = 0
    private val clickDelay = 300

    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < clickDelay) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View)
}