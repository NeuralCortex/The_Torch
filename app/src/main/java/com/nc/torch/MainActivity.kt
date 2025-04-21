package com.nc.torch

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nc.torch.adapter.ColorSpinnerAdapter
import com.nc.torch.viewmodel.RandomViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var flashLight: FlashLight
    private lateinit var content: View
    private val viewModel: RandomViewModel by viewModels()
    private lateinit var toggleFlash: ToggleButton

    @Parcelize
    data class ColorItem(val color: String) : Parcelable {
        private companion object : Parceler<ColorItem> {
            override fun ColorItem.write(parcel: Parcel, flags: Int) {
                parcel.writeString(color)
            }

            override fun create(parcel: Parcel): ColorItem {
                return ColorItem(parcel.readString().toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashLight = FlashLight(this)

        val colorList = arrayListOf<ColorItem>(
            ColorItem("#FFFFFF"),
            ColorItem("#000000"),
            ColorItem("#FF0000"),
            ColorItem("#00FF00"),
            ColorItem("#0000FF"),
            ColorItem("#FFFF00"),
            ColorItem("#00FFFF"),
            ColorItem("#FF00FF")
        )

        content = findViewById<View>(R.id.content)
        val btnFullscreen = findViewById<Button>(R.id.btnFullscreen)
        toggleFlash = findViewById<ToggleButton>(R.id.toggleFlash)
        val cbHypno = findViewById<CheckBox>(R.id.cbHypno)

        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.adapter = ColorSpinnerAdapter(this, colorList)
        spinner.onItemSelectedListener = this
        spinner.setSelection(1)

        btnFullscreen.setOnClickListener {
            toggleFlash.isChecked = false

            val intent = Intent(this, FullscreenActivity::class.java)
            val selColor = spinner.selectedItem as ColorItem

            intent.putExtra("COLOR", selColor.color.toColorInt())
            intent.putParcelableArrayListExtra("COLOR_LIST", colorList)
            intent.putExtra("HYPNO", cbHypno.isChecked)

            startActivity(intent)
        }

        toggleFlash.setOnClickListener {
            if (!cbHypno.isChecked) {
                flashLight.enableFlash(toggleFlash.isChecked)
            } else {
                setupBlinking()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupBlinking() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isBlinking.collect { shouldBlink ->
                    blinkFlashlight()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun blinkFlashlight() {
        while (toggleFlash.isChecked) {
            try {
                flashLight.enableFlash(true)
                delay(viewModel.blinkDelay.value)
                flashLight.enableFlash(false)
                delay(viewModel.blinkDelay.value)
            } catch (e: Exception) {
                Log.e("Flashlight", "Blinking error: ${e.message}")
                break
            }
        }
    }


    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        (parent?.getItemAtPosition(position) as? ColorItem)?.let {
            content.setBackgroundColor(it.color.toColorInt())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        content.setBackgroundColor(
            ContextCompat.getColor(
                this@MainActivity.baseContext,
                R.color.black
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        flashLight.enableFlash(false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        super.onDestroy()
        flashLight.enableFlash(false)
    }
}