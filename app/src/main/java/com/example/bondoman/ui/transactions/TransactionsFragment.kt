package com.example.bondoman.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.database.AppDatabase
import com.example.bondoman.database.Transaction
import com.example.bondoman.databinding.FragmentTransactionsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : TransactionsViewModel
    private lateinit var rv: RecyclerView
    private lateinit var addTransactionButton: FloatingActionButton

    private var listOfTransaction: MutableList<Transaction> = mutableListOf()
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
    ): View? {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        rv = binding.rvTransaction
        rv.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val transactions = AppDatabase.getInstance(requireContext()).transactionDao().index("13521007@std.stei.itb.ac.id")
            val transactionAdapter = TransactionAdapter(requireActivity(), transactions.toMutableList(), requireActivity().supportFragmentManager, viewModel) { transactionId ->
                val action = TransactionsFragmentDirections.actionNavigationTransactionsToTransactionUpdate(transactionId)
                findNavController().navigate(action)
            }
            binding.rvTransaction.adapter = transactionAdapter
            binding.rvTransaction.layoutManager = LinearLayoutManager(requireContext())
        }
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