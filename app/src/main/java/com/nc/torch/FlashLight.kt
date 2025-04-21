package com.nc.torch

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class FlashLight(
    private val context: Context
) {
    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private var cameraId: String? = null

    init {
        initializeCamera()
    }

    private fun initializeCamera() {
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Camera access error", e)
        }
    }

    fun isFlashAvailable(): Boolean {
        return cameraId != null && context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun enableFlash(enabled: Boolean) {
        try {
            cameraId?.let { safeCameraId ->
                cameraManager.setTorchMode(safeCameraId, enabled)
            } ?: run {
                Log.w(TAG, "No camera with flash available")
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error setting torch mode", e)
        }
    }

    companion object {
        private const val TAG = "FlashLight"

        @RequiresApi(Build.VERSION_CODES.M)
        fun hasFlashFeature(context: Context): Boolean {
            return context.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        }
    }
}
