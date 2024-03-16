package com.example.bondoman.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.bondoman.R
import com.example.bondoman.database.AppDatabase
import com.example.bondoman.database.Transaction
import com.example.bondoman.databinding.FragmentTransactionUpdateBinding
import com.example.bondoman.repository.TransactionRepository

class TransactionUpdate : Fragment() {
    private var _binding: FragmentTransactionUpdateBinding? = null
    private var currentTransaction: Transaction? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionUpdateBinding.inflate(inflater, container, false)
        val transactionsViewModel: TransactionsViewModel by viewModels {
            TransactionsViewModelFactory(TransactionRepository(AppDatabase.getInstance(requireContext())))
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.income_outcome_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.etKategori.adapter = adapter
        }
        val transactionId = TransactionUpdateArgs.fromBundle(requireArguments()).transactionId
        transactionsViewModel.getTransactionById(transactionId)
        transactionsViewModel.transaction.observe(viewLifecycleOwner) { transaction ->
            currentTransaction = transaction
            binding.etTitle.setText(transaction!!.transaction_name.toString())
            binding.etKategori.setSelection(
                if (transaction.transaction_category == "Income") 0 else 1
            )
            binding.etNominal.setText(transaction.transaction_price.toString())
            binding.etLokasi.setText(transaction.transaction_location.toString())
        }
        binding.saveButton.setOnClickListener{
            currentTransaction?.let { transaction ->
                val updatedTransaction = transaction.copy(
                    transaction_name = binding.etTitle.text.toString(),
                    transaction_category = if (binding.etKategori.selectedItemPosition == 0) "Income" else "Outcome",
                    transaction_price = binding.etNominal.text.toString().toIntOrNull() ?: 0,
                    transaction_location = binding.etLokasi.text.toString()
                )
                transactionsViewModel.updateTransaction(updatedTransaction)
                Toast.makeText(requireContext(), "Transaction updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}