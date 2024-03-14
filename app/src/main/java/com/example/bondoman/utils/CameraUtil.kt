package com.example.bondoman.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraUtil(
    private val view: PreviewView,
) {
    private var activity: AppCompatActivity? = null
    private lateinit var context: Context
    private var executor: ExecutorService? = null
    private var fragment: Fragment? = null
    private lateinit var cameraProvider: ProcessCameraProvider
    var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val preview = Preview.Builder()
        .build()
        .also {
            it.setSurfaceProvider(view.surfaceProvider)
        }

    fun setup(
        activity: AppCompatActivity,
        afterSetup: ((CameraUtil)->Unit)? = null,
    ) {
        this.activity = activity
        this.context = activity.baseContext
        this.setup(afterSetup)
    }

    fun setup(
        fragment: Fragment,
        afterSetup: ((CameraUtil) -> Unit)? = null,
    ) {
        this.fragment = fragment
        this.context = fragment.requireContext()
        this.setup(afterSetup)
    }

    private fun setup(
        afterSetup: ((CameraUtil) -> Unit)? = null,
    ) {
        var isGranted = true
        if (!allPermissionsGranted(context)) {
            (fragment ?: activity)?.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                if (!it) {
                    isGranted = false
                    Toast.makeText(
                        context,
                        "Camera permission not granted!.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }?.launch(REQUIRED_PERMISSIONS[0])
        }
        if (isGranted) {
            executor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()
                afterSetup?.invoke(this)
            }, ContextCompat.getMainExecutor(context))
        }
    }

    fun startCamera(
        vararg useCases: UseCase
    ) {
        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()
            // Bind use cases to camera
            (fragment ?: activity)?.let {
                cameraProvider.bindToLifecycle(
                    it,
                    cameraSelector,
                    preview,
                    *useCases,
                )
            }
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    fun stopCamera() {
        cameraProvider.unbindAll()
        executor?.shutdown()
    }

    companion object {
        const val TAG = "Bondoman"
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).toTypedArray()
        private fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
