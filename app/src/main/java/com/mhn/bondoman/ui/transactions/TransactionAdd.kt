package com.mhn.bondoman.ui.transactions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.FragmentAddTransactionBinding
import com.mhn.bondoman.utils.LocationAdapter
import java.time.LocalDate

class TransactionAdd : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var etTitle: TextInputEditText
    private lateinit var etNominal: TextInputEditText
    private lateinit var etKategori: Spinner
    private lateinit var etLocation: TextInputEditText
    private lateinit var addButton: Button
    private lateinit var gpsService: LocationAdapter
    private lateinit var transactionLocation: String
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var taViewModel: TransactionAddViewModel
    private var gMap: GoogleMap? = null
    private lateinit var transactionCoordinate: LatLng
    private var onMapReadyCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            TransactionsViewModel.FACTORY
        )[TransactionsViewModel::class.java]

        taViewModel = ViewModelProvider(requireActivity())[TransactionAddViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        gpsService = LocationAdapter.getInstance(requireActivity())

        etTitle = binding.etTitle
        etNominal = binding.etNominal
        etKategori = binding.etKategori

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.income_outcome_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            etKategori.adapter = adapter
        }

        etLocation = binding.etLokasi
        addButton = binding.saveButton
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLocation()
        addButton.setOnClickListener {
            // Save the Items into the Database
            val email = KeyStoreManager.getInstance(requireContext()).getEmail()
            if (email == null) {
                Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (etNominal.text.toString().toIntOrNull() == null) {
                Toast.makeText(requireContext(), "Nominal must be a number", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (etTitle.text.toString().isEmpty() || etNominal.text.toString()
                    .isEmpty() || etLocation.text.toString().isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                val newTransaction = Transaction(
                    null,
                    email,
                    transaction_name = etTitle.text.toString(),
                    transaction_price = etNominal.text.toString().toInt(),
                    transaction_category = etKategori.selectedItem.toString(),
                    transaction_location = etLocation.text.toString(),
                    transaction_date = LocalDate.now().toString(),
                    transaction_latitude = transactionCoordinate.latitude,
                    transaction_longitude = transactionCoordinate.longitude
                )
                viewModel.addTransaction(newTransaction)
                // redirect to the transaction list
                val action = TransactionAddDirections.actionTransactionAddToNavigationTransactions()
                requireView().findNavController().navigate(action)
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        etTitle.setText(taViewModel.getTitle())
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val selectedTitle = intent?.getStringExtra("selectedTitle")
                taViewModel.setTitle(selectedTitle!!)
                Log.d("RANDOMIZE", taViewModel.getTitle())
            }
        }
        val filter = IntentFilter("com.mhn.bondoman.RANDOMIZE_TRANSACTION")
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        ContextCompat.registerReceiver(requireContext(), broadcastReceiver, filter, receiverFlags)
    }

    override fun onDestroy() {
        super.onDestroy()
        taViewModel.setTitle("")
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        onMapReadyCallback?.invoke()
    }

    private fun getLocation() {
        addButton.visibility = View.GONE
        if(gMap == null){
            onMapReadyCallback = {
                onMapReadyCallback = null
                getLocation()
            }
        } else {
            try {
                gpsService.getLocation { location ->
                    transactionLocation = gpsService.transformToReadable(location)
                    etLocation.setText(transactionLocation)
                    val loc = gpsService.getCurrentCoordinates()!!
                    transactionCoordinate = LatLng(loc.latitude, loc.longitude)
                    gMap?.addMarker(
                        MarkerOptions().position(transactionCoordinate)
                            .title("Transaction Location")
                    )
                    gMap?.moveCamera(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                            transactionCoordinate,
                            15f
                        )
                    )
                    addButton.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Please allow location", Toast.LENGTH_SHORT).show()
            }
        }
    }
}