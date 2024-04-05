package com.mhn.bondoman.ui.transactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.mhn.bondoman.R
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.FragmentTransactionUpdateBinding
import com.mhn.bondoman.utils.LocationAdapter

class TransactionUpdate : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentTransactionUpdateBinding? = null
    private var currentTransaction: Transaction? = null
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var gpsService: LocationAdapter
    private lateinit var etTitle: TextInputEditText
    private lateinit var etNominal: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var updateLocationButton: Button
    private lateinit var transactionLocation: String
    private lateinit var transactionCoordinate: LatLng
    private lateinit var gMap: GoogleMap

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            TransactionsViewModel.FACTORY
        )[TransactionsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionUpdateBinding.inflate(inflater, container, false)
        gpsService = LocationAdapter.getInstance(requireActivity())

        etTitle = binding.etTitle
        etNominal = binding.etNominal
        etLocation = binding.etLokasi
        saveButton = binding.saveButton
        updateLocationButton = binding.updateLocationButton

        val transactionId = TransactionUpdateArgs.fromBundle(requireArguments()).transactionId
        viewModel.getTransactionById(transactionId)

        viewModel.transaction.observe(viewLifecycleOwner) { transaction ->
            currentTransaction = transaction
            binding.etTitle.setText(transaction!!.transaction_name.toString())
            binding.etNominal.setText(transaction.transaction_price.toString())
            binding.etLokasi.setText(transaction.transaction_location.toString())
            transactionCoordinate = LatLng(
                transaction.transaction_latitude,
                transaction.transaction_longitude
            )
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        binding.updateLocationButton.setOnClickListener {
            updateLocationButton.visibility = View.GONE
            getLocation()
            Log.d("Updated Location", transactionCoordinate.toString())
        }

        binding.saveButton.setOnClickListener {
            currentTransaction?.let { transaction ->
                val updatedTransaction = transaction.copy(
                    transaction_name = etTitle.text.toString(),
                    transaction_price = etNominal.text.toString().toIntOrNull() ?: 0,
                    transaction_location = etLocation.text.toString(),
                    transaction_latitude = transactionCoordinate.latitude,
                    transaction_longitude = transactionCoordinate.longitude
                )
                viewModel.updateTransaction(updatedTransaction)
                Toast.makeText(
                    requireContext(),
                    "Transaction updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                val action =
                    TransactionUpdateDirections.actionTransactionUpdateToNavigationTransactions()
                requireView().findNavController().navigate(action)
            }
        }

        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.addMarker(MarkerOptions().position(transactionCoordinate).title("Transaction Location"))
        gMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(transactionCoordinate, 15f))
    }

    private fun getLocation() {
        saveButton.visibility = View.GONE
        try {
            gpsService.getLocation { location ->
                transactionLocation = gpsService.transformToReadable(location)
                etLocation.setText(transactionLocation)
                val loc = gpsService.getCurrentCoordinates()!!
                transactionCoordinate = LatLng(loc.latitude, loc.longitude)
                gMap.addMarker(MarkerOptions().position(transactionCoordinate).title("Transaction Location"))
                gMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(transactionCoordinate, 15f))
                saveButton.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please allow location", Toast.LENGTH_SHORT).show()
        }
    }
}