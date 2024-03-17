package com.mhn.bondoman.ui.transactions

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
import com.mhn.bondoman.database.AppDatabase
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.FragmentTransactionsBinding
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
            val email = KeyStoreManager.getInstance(requireContext()).getEmail()
            if (email == null) {
                findNavController().navigate(TransactionsFragmentDirections.actionNavigationTransactionsToLoginActivity())
            } else {
                val transactions = AppDatabase.getInstance(requireContext()).transactionDao().index(email)
                val transactionAdapter = TransactionAdapter(requireActivity(), transactions.toMutableList(), requireActivity().supportFragmentManager, viewModel) { transactionId ->
                    val action = TransactionsFragmentDirections.actionNavigationTransactionsToTransactionUpdate(transactionId)
                    findNavController().navigate(action)
                }
                binding.rvTransaction.adapter = transactionAdapter
                binding.rvTransaction.layoutManager = LinearLayoutManager(requireContext())
            }
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