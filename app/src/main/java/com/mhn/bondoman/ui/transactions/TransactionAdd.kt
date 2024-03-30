package com.mhn.bondoman.ui.transactions

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.mhn.bondoman.R
import com.mhn.bondoman.database.AppDatabase
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.FragmentAddTransactionBinding
import com.mhn.bondoman.utils.LocationAdapter
import com.mhn.bondoman.utils.RandomizeReceiver
import kotlinx.coroutines.launch
import java.time.LocalDate

class TransactionAdd : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var etTitle: TextInputEditText
    private lateinit var etNominal: TextInputEditText
    private lateinit var etKategori: Spinner
    private lateinit var etLocation: TextInputEditText
    private lateinit var addButton: Button
    private lateinit var gpsService: LocationAdapter
    private lateinit var transactionLocation: String
    private lateinit var broadcastReceiver: RandomizeReceiver
    private lateinit var viewModel: TransactionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            TransactionsViewModel.FACTORY
        )[TransactionsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        broadcastReceiver = RandomizeReceiver.getInstance()

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
                    transaction_date = LocalDate.now().toString()
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
        try {
            gpsService.getLocation { location ->
                transactionLocation = gpsService.transformToReadable(location)
                etLocation.setText(transactionLocation)
            }
            binding.etTitle.setText(broadcastReceiver.selectedTitle)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please allow location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        broadcastReceiver = RandomizeReceiver.getInstance()
        val filter = IntentFilter("com.mhn.bondoman.RANDOMIZE_TRANSACTION")
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        ContextCompat.registerReceiver(requireContext(), broadcastReceiver, filter, receiverFlags)
    }
}