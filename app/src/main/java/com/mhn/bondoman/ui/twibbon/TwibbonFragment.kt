package com.mhn.bondoman.ui.twibbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mhn.bondoman.R
import com.mhn.bondoman.databinding.FragmentTwibbonBinding
import com.mhn.bondoman.utils.CameraAdapter

class TwibbonFragment : Fragment() {

    companion object {
        fun newInstance() = TwibbonFragment()
    }

    private val viewModel: TwibbonViewModel by viewModels()
    private lateinit var binding: FragmentTwibbonBinding
    private lateinit var cameraAdapter: CameraAdapter

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
            CameraAdapter(cameraView).setup(this@TwibbonFragment) {
                cameraAdapter = it
                twibbonChangeCameraBtn.setOnClickListener(changeCamera)
                it.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                setCaptureButton(it)
            }
            return root
        }
    }

    private val changeCamera = View.OnClickListener {
        cameraAdapter.stopCamera()
        cameraAdapter.cameraSelector = if (cameraAdapter.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraAdapter.startCamera()
    }

    private fun setCaptureButton(cameraAdapter: CameraAdapter) {
        with(binding) {
            cameraAdapter.startCamera()
            captureBtn.setOnClickListener {
                cameraAdapter.stopCamera()
                captureBtn.text = "Retake"
                captureBtn.setOnClickListener {
                    captureBtn.text = "Take"
                    setCaptureButton(cameraAdapter)
                }
            }
        }
    }
}