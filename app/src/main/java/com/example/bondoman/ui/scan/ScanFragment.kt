package com.example.bondoman.ui.scan

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bondoman.R
import com.example.bondoman.api.BondomanApi
import com.example.bondoman.api.KeyStoreManager
import com.example.bondoman.databinding.FragmentScanBinding
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
    private val REQUEST_IMAGE_CAPTURE = 100
    private val IMAGE_REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        binding.cameraButton.setOnClickListener {
            takePicture()
        }
        binding.mediaButton.setOnClickListener {
            selectImage()
        }
        binding.uploadButton.setOnClickListener {
            uploadImage()
        }
        return binding.root
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            Toast.makeText(context, "Error taking picture", Toast.LENGTH_SHORT).show()
        }
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
                val file = binding.imageCapture.drawable
                val keyStoreManager = KeyStoreManager(requireContext())
                val token = keyStoreManager.getToken("token")
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1) {
            try {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.imageCapture.setImageBitmap(imageBitmap)
            } catch (e: Exception) {
                Log.d("ScanFragment", "Error taking picture")
            }
        } else if (requestCode == IMAGE_REQUEST_CODE && resultCode == -1) {
            try {
                binding.imageCapture.setImageURI(data?.data)
            } catch (e: Exception) {
                Log.d("ScanFragment", "Error selecting image")
            }
        } else {
            Log.d("ScanFragment", "Error activity result")
        }
    }
}