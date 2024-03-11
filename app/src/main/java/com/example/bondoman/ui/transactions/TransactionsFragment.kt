package com.example.bondoman.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmentTransactionsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var rv: RecyclerView
    private lateinit var addTransactionButton: FloatingActionButton

    private val listOfTransaction: MutableList<Transaction> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        listOfTransaction.add(Transaction("2024-03-10", "Store A", "City X", "$10.00", "Groceries"))
        listOfTransaction.add(Transaction("2024-03-09", "Restaurant B", "City Y", "$25.00", "Dining"))

        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        rv = binding.rvTransaction
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = TransactionAdapter(listOfTransaction)

        addTransactionButton = binding.fabAddTransaction
        addTransactionButton.setOnClickListener{
            val action = TransactionsFragmentDirections.actionNavigationTransactionsToTransactionAdd()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}