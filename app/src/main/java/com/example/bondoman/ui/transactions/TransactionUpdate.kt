package com.example.bondoman.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.bondoman.database.AppDatabase
import com.example.bondoman.databinding.FragmentTransactionUpdateBinding
import com.example.bondoman.repository.TransactionRepository

class TransactionUpdate : Fragment() {
    private var _binding: FragmentTransactionUpdateBinding? = null
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
        val transactionId = TransactionUpdateArgs.fromBundle(requireArguments()).transactionId
        transactionsViewModel.getTransactionById(transactionId)
        transactionsViewModel.transaction.observe(viewLifecycleOwner) { transaction ->
            binding.etTitle.setText(transaction!!.transaction_name.toString())
            binding.etKategori.setSelection(
                if (transaction.transaction_category == "Income") 1 else 2
            )
            binding.etNominal.setText(transaction.transaction_price.toString())
            binding.etLokasi.setText(transaction.transaction_location.toString())
        }
        return binding.root
    }

}