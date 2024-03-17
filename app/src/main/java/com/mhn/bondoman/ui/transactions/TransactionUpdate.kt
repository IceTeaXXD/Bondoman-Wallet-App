package com.mhn.bondoman.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.FragmentTransactionUpdateBinding
import com.mhn.bondoman.utils.LocationAdapter
import com.google.android.material.textfield.TextInputEditText

class TransactionUpdate : Fragment() {
    private var _binding: FragmentTransactionUpdateBinding? = null
    private var currentTransaction: Transaction? = null
    private lateinit var viewModel : TransactionsViewModel
    private lateinit var gpsService: LocationAdapter
    private lateinit var etTitle: TextInputEditText
    private lateinit var etNominal: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var transactionLocation: String

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
    ): View? {
        _binding = FragmentTransactionUpdateBinding.inflate(inflater, container, false)
        gpsService = LocationAdapter.getInstance(requireActivity())

        etTitle = binding.etTitle
        etNominal = binding.etNominal
        etLocation = binding.etLokasi

        val transactionId = TransactionUpdateArgs.fromBundle(requireArguments()).transactionId
        viewModel.getTransactionById(transactionId)

        viewModel.transaction.observe(viewLifecycleOwner) { transaction ->
            currentTransaction = transaction
            binding.etTitle.setText(transaction!!.transaction_name.toString())
            binding.etNominal.setText(transaction.transaction_price.toString())
            binding.etLokasi.setText(transaction.transaction_location.toString())
        }
        binding.saveButton.setOnClickListener{
            currentTransaction?.let { transaction ->
                val updatedTransaction = transaction.copy(
                    transaction_name = etTitle.text.toString(),
                    transaction_price = etNominal.text.toString().toIntOrNull() ?: 0,
                    transaction_location = etLocation.text.toString()
                )
                viewModel.updateTransaction(updatedTransaction)
                Toast.makeText(requireContext(), "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                val action = TransactionUpdateDirections.actionTransactionUpdateToNavigationTransactions()
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
        } catch (e: Exception){
            Toast.makeText(requireContext(), "Please allow location", Toast.LENGTH_SHORT).show()
        }
    }
}