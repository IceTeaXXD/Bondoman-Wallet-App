package com.example.bondoman.ui.twibbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmentTwibbonBinding
import com.example.bondoman.utils.CameraUtil

class TwibbonFragment : Fragment() {

    companion object {
        fun newInstance() = TwibbonFragment()
    }

    private val viewModel: TwibbonViewModel by viewModels()
    private lateinit var binding: FragmentTwibbonBinding
    private lateinit var cameraUtil: CameraUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_twibbon, container, false)
        with (binding) {
            cameraView.previewStreamState.observe(viewLifecycleOwner) {
                if (it == PreviewView.StreamState.STREAMING) {
                    cameraView.overlay.add(twibbonView)
                }
            }
            CameraUtil(cameraView).setup(this@TwibbonFragment) {
                cameraUtil = it
                twibbonChangeCameraBtn.setOnClickListener(changeCamera)
                it.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                setCaptureButton(it)
            }
            return root
        }
    }

    private val changeCamera = View.OnClickListener {
        cameraUtil.stopCamera()
        cameraUtil.cameraSelector = if (cameraUtil.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraUtil.startCamera()
    }

    private fun setCaptureButton(cameraUtil: CameraUtil) {
        with(binding) {
            cameraUtil.startCamera()
            captureBtn.setOnClickListener {
                cameraUtil.stopCamera()
                captureBtn.text = "Retake"
                captureBtn.setOnClickListener {
                    setCaptureButton(cameraUtil)
                }
            }
        }
    }
}