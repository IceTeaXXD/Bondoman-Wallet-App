package com.mhn.bondoman.ui.scan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhn.bondoman.databinding.FragmentScanResultBinding

class ScanResult : Fragment() {
    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScanResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)
        val recyclerView = binding.rvScanResponse
        val dummy = mutableListOf(
            ScanResultData("Item 1", 9.99, 1),
            ScanResultData("Item 2", 19.99, 2),
            ScanResultData("Item 3", 29.99, 3),
        )
        Log.d("Adapter", dummy.toString())
        adapter = ScanResultAdapter(requireActivity(),dummy)
        binding.rvScanResponse.adapter = adapter
        binding.rvScanResponse.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }
    companion object {
        fun createDummyScanResultData(): MutableList<ScanResultData> {
            return mutableListOf(
                ScanResultData("Item 1", 9.99, 1),
                ScanResultData("Item 2", 19.99, 2),
                ScanResultData("Item 3", 29.99, 3),
            )
        }
    }
}