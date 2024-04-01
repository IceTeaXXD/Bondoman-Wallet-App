package com.mhn.bondoman.ui.scan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.FragmentScanResultBinding
import com.mhn.bondoman.models.Item
import com.mhn.bondoman.models.Items
import com.mhn.bondoman.ui.transactions.TransactionUpdateDirections
import com.mhn.bondoman.ui.transactions.TransactionsViewModel
import com.mhn.bondoman.utils.LocationAdapter
import java.time.LocalDate

class ScanResult : Fragment() {
    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var gpsService: LocationAdapter
    private lateinit var adapter: ScanResultAdapter
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var transactionLocation: String

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
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)

        gpsService = LocationAdapter.getInstance(requireActivity())
        getLocation()
        val recyclerView = binding.rvScanResponse
        viewModel.scanResultData.observe(viewLifecycleOwner, Observer {resultData ->
            resultData.let {
                it.items.let {
                    for (item in it.items){
                        Log.d("Real data ", item.toString())
                    }
                    adapter = ScanResultAdapter(requireActivity(), it.items.toMutableList())
                    binding.rvScanResponse.adapter = adapter
                    binding.rvScanResponse.layoutManager = LinearLayoutManager(requireContext())
                    binding.fabAddTransaction.setOnClickListener{
                        saveResponse()
                    }
                }
            }
        })
        return binding.root
    }
    fun saveResponse(){
        viewModel.scanResultData.observe(viewLifecycleOwner, Observer {resultData ->
            val email = KeyStoreManager.getInstance(requireContext()).getEmail()
            resultData.let {
                it.items.let {
                    for (item in it.items){
                        val _transaction = Transaction(
                            null,
                            email!!,
                            transaction_name = item.name,
                            transaction_price = (item.price * item.qty).toInt(),
                            transaction_category = "Outcome",
                            transaction_date= LocalDate.now().toString(),
                            transaction_location = transactionLocation,
//                            transaction_latitude = 0.0,
//                            transaction_longitude = 0.0
                        )
                        viewModel.addTransaction(_transaction)
                    }
                }
            }
        })
        val action = ScanResultDirections.actionNavigationScanResultToNavigationTransactions()
        findNavController().navigate(action)
    }

    fun getLocation() {
        try {
            gpsService.getLocation { location ->
                transactionLocation = gpsService.transformToReadable(location)
                binding.fabAddTransaction.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please allow location", Toast.LENGTH_SHORT).show()
        }
    }
}