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
import com.example.bondoman.databinding.FragmentScanBinding

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
        val selectImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(selectImageIntent, IMAGE_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(context, "Error selecting image", Toast.LENGTH_SHORT).show()
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