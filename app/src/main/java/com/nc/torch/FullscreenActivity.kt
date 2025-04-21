package com.nc.torch

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nc.torch.listener.DoubleClickListener
import com.nc.torch.viewmodel.RandomViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class FullscreenActivity : AppCompatActivity(){

    private lateinit var colorList: ArrayList<MainActivity.ColorItem>
    private val viewModel: RandomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        hideSystemBars()

        setContentView(R.layout.activity_fullscreen)

        val color = intent.getIntExtra("COLOR", R.color.white)
        val isHypno = intent.getBooleanExtra("HYPNO", false)
        colorList = intent.getParcelableArrayListExtra<MainActivity.ColorItem>("COLOR_LIST")!!

        val fullScreen = findViewById<View>(R.id.fullscreen)

        if (isHypno) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.randomFlow.collect { idx ->
                        fullScreen.setBackgroundColor(colorList[idx].color.toColorInt())
                    }
                }
            }

            viewModel.startGeneration(colorList.size)
        } else {
            fullScreen.setBackgroundColor(color)
        }

        fullScreen.setOnClickListener(
            object : DoubleClickListener() {
                override fun onDoubleClick(v: View) {
                    viewModel.stopGeneration()
                    finish()
                }
            }
        )

        val layoutParams = window.attributes.apply {
            screenBrightness = 1f
        }
        window.attributes = layoutParams
    }

    private fun hideSystemBars() {
        when {
            Build.VERSION.SDK_INT >= 30 -> {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                @Suppress("DEPRECATION")
                ViewCompat.getWindowInsetsController(window.decorView)?.apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }

            else -> {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        )
            }
        }
    }
}