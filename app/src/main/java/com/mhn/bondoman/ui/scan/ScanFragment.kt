package com.mhn.bondoman.ui.scan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mhn.bondoman.R
import com.mhn.bondoman.api.BondomanApi
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.databinding.FragmentScanBinding
import com.mhn.bondoman.ui.transactions.TransactionsViewModel
import com.mhn.bondoman.utils.CameraAdapter
import com.mhn.bondoman.utils.NetworkAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ScanFragment : Fragment(), NetworkAdapter.NetworkListener {
    companion object {
        fun newInstance() = ScanFragment()
    }
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var binding: FragmentScanBinding
    private lateinit var cameraView: PreviewView
    private lateinit var switchButton: ImageButton
    private lateinit var buttonContainer: LinearLayout
    private val IMAGE_REQUEST_CODE = 101
    private lateinit var cameraAdapter: CameraAdapter
    private lateinit var networkAdapter: NetworkAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            TransactionsViewModel.FACTORY
        )[TransactionsViewModel::class.java]
        networkAdapter = NetworkAdapter.getInstance(requireContext())
        networkAdapter.subscribe(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        if(networkAdapter.isNetworkConnected()) {
            with(binding) {
                CameraAdapter(cameraView).setup(this@ScanFragment) {
                    cameraAdapter = it
                    switchButton.setOnClickListener(changeCamera)
                    it.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    setCaptureButton(it)
                }
                binding.mediaButton.setOnClickListener {
                    selectImage()
                }
                binding.uploadButton.setOnClickListener {
                    uploadImage()
                }
            }
        }else{
            binding.cameraView.visibility = View.INVISIBLE
            binding.buttonContainer.visibility = View.INVISIBLE
            binding.noNetwork?.backButton?.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        cameraView = binding.cameraView
        buttonContainer = binding.buttonContainer
        switchButton = binding.switchButton
        return binding.root
    }

    private fun setCaptureButton(cameraAdapter: CameraAdapter) {
        with(binding) {
            cameraAdapter.startCamera()
            imageView.setImageURI(null)
            imageView.visibility = View.GONE
            cameraView.visibility = View.VISIBLE
            uploadButton.visibility = View.GONE
            switchButton.visibility = View.VISIBLE
            cameraButton.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
            Log.i("Camera", "Camera started")
            cameraButton.setOnClickListener {
                cameraAdapter.stopCamera()
                uploadButton.visibility = View.VISIBLE
                switchButton.visibility = View.GONE
                imageView.setImageDrawable(cameraAdapter.getBitmap()?.toDrawable(resources))
                cameraButton.setImageDrawable(resources.getDrawable(R.drawable.ic_x))
                cameraButton.setOnClickListener {
                    setCaptureButton(cameraAdapter)
                }
            }
        }
    }

    private val changeCamera = View.OnClickListener {
        cameraAdapter.stopCamera()
        cameraAdapter.cameraSelector =
            if (cameraAdapter.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
        cameraAdapter.startCamera()
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
                val token = KeyStoreManager.getInstance(requireContext()).getToken()
                val requestFile =
                    file.toString().toRequestBody("image/jpg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
                val response = BondomanApi.getInstance().uploadBill("Bearer $token", body)
                viewModel.addItemFromScanner(response.body()!!)
                Log.i("Upload", "Response: $response")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Upload Error", "Error: ${e.message}")
                    Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val action = ScanFragmentDirections.actionScanToNavigationScanResult()
        findNavController().navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == -1) {
            try {
                binding.imageView.setImageDrawable(data?.data?.let {
                    requireContext().contentResolver.openInputStream(it)?.let { it1 ->
                        Bitmap.createBitmap(
                            BitmapFactory.decodeStream(it1)
                        )
                    }
                }?.toDrawable(resources))
                binding.imageView.visibility = View.VISIBLE
                binding.cameraView.visibility = View.GONE
                binding.uploadButton.visibility = View.VISIBLE
                binding.switchButton.visibility = View.GONE
                binding.cameraButton.setImageDrawable(resources.getDrawable(R.drawable.ic_x))
                binding.cameraButton.setOnClickListener { setCaptureButton(cameraAdapter) }
                Log.d("ScanFragment", "Image selected")
            } catch (e: Exception) {
                Log.d("ScanFragment", "Error selecting image")
            }
        } else {
            Log.d("ScanFragment", "Error activity result")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkAdapter.unsubscribe(this)
    }

    override fun onNetworkAvailable() {
        requireActivity().runOnUiThread {
            cameraView.visibility = View.VISIBLE
            buttonContainer.visibility = View.VISIBLE
            CameraAdapter(cameraView).setup(this@ScanFragment) {
                cameraAdapter = it
                switchButton.setOnClickListener(changeCamera)
                it.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                setCaptureButton(it)
            }
        }
    }

    override fun onNetworkLost() {
        requireActivity().runOnUiThread {
            cameraView.visibility = View.INVISIBLE
            buttonContainer.visibility = View.INVISIBLE
            binding.noNetwork?.backButton?.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}