package com.example.bondoman.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.R
import com.example.bondoman.database.AppDatabase
import com.example.bondoman.database.Transaction
import com.example.bondoman.databinding.FragmentAddTransactionBinding
import com.example.bondoman.gps.BondomanLocationService
import com.google.android.material.textfield.TextInputEditText
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
    private lateinit var gpsService: BondomanLocationService
    private lateinit var transactionLocation: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        gpsService = BondomanLocationService.getInstance(requireActivity())

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
            val transactionDao = AppDatabase.getInstance(requireContext()).transactionDao()
            val newTransaction = Transaction(
                null,
                "13521007@std.stei.itb.ac.id",
                transaction_name = etTitle.text.toString(),
                transaction_price = etNominal.text.toString().toInt(),
                transaction_category = etKategori.selectedItem.toString(),
                transaction_location = etLocation.text.toString(),
                transaction_date = LocalDate.now().toString()
            )

            viewLifecycleOwner.lifecycleScope.launch {
                transactionDao.store(newTransaction)
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        gpsService.getLocation { location ->
            transactionLocation = gpsService.transformToReadable(location)
            etLocation.setText(transactionLocation)
        }
    }
}