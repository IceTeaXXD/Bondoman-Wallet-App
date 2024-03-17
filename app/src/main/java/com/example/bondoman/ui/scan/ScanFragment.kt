package com.example.bondoman.ui.scan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmentScanBinding
import com.example.bondoman.api.BondomanApi
import com.example.bondoman.database.KeyStoreManager
import com.example.bondoman.utils.CameraUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ScanFragment : Fragment() {

    companion object {
        fun newInstance() = ScanFragment()
    }

    private lateinit var binding: FragmentScanBinding
    private val IMAGE_REQUEST_CODE = 101
    private lateinit var cameraUtil: CameraUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        with (binding) {
            CameraUtil(cameraView).setup(this@ScanFragment) {
                cameraUtil = it
                switchButton.setOnClickListener(changeCamera)
                it.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                setCaptureButton(it)
            }
            binding.mediaButton.setOnClickListener {
                selectImage()
            }
            binding.uploadButton.setOnClickListener {
                uploadImage()
            }
            return root
        }
    }

    private fun setCaptureButton(cameraUtil: CameraUtil) {
        with(binding) {
            cameraUtil.startCamera()
            imageView.setImageURI(null)
            imageView.visibility = View.GONE
            cameraView.visibility = View.VISIBLE
            uploadButton.visibility = View.GONE
            switchButton.visibility = View.VISIBLE
            cameraButton.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
            Log.i("Camera", "Camera started")
            cameraButton.setOnClickListener {
                cameraUtil.stopCamera()
                uploadButton.visibility = View.VISIBLE
                switchButton.visibility = View.GONE
                imageView.setImageDrawable(cameraUtil.getBitmap()?.toDrawable(resources))
                cameraButton.setImageDrawable(resources.getDrawable(R.drawable.ic_x))
                cameraButton.setOnClickListener {
                    setCaptureButton(cameraUtil)
                }
            }
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


    private fun selectImage() {
        val selectImageIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(selectImageIntent, IMAGE_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(context, "Error selecting image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val file = binding.imageView.drawable
                val keyStoreManager = KeyStoreManager(requireContext())
                val token = keyStoreManager.getToken()
                // TODO check if token not found or expired
                if (token == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Token not found", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val requestFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), file.toString())
                val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
                val response = BondomanApi.getInstance().uploadBill("Bearer $token", body)
                Log.i("Upload", "Response: ${response}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Upload Error", "Error: ${e.message}")
                    Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == -1) {
            try {
                binding.imageView.setImageDrawable(data?.data?.let { requireContext().contentResolver.openInputStream(it)?.let { it1 -> Bitmap.createBitmap(
                    BitmapFactory.decodeStream(it1)) } }?.toDrawable(resources))
                binding.imageView.visibility = View.VISIBLE
                binding.cameraView.visibility = View.GONE
                binding.uploadButton.visibility = View.VISIBLE
                binding.switchButton.visibility = View.GONE
                binding.cameraButton.setImageDrawable(resources.getDrawable(R.drawable.ic_x))
                binding.cameraButton.setOnClickListener { setCaptureButton(cameraUtil) }
                Log.d("ScanFragment", "Image selected")
            } catch (e: Exception) {
                Log.d("ScanFragment", "Error selecting image")
            }
        } else {
            Log.d("ScanFragment", "Error activity result")
        }
    }
}