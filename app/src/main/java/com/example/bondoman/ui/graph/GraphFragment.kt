package com.example.bondoman.ui.graph

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bondoman.R
import androidx.databinding.DataBindingUtil
import com.db.williamchart.data.configuration.DonutChartConfiguration
import com.db.williamchart.view.DonutChartView
import com.example.bondoman.databinding.FragmentGraphBinding

class GraphFragment : Fragment() {

    companion object {
        fun newInstance() = GraphFragment()
        private val donutSet = listOf(
            20f,
            80f,
            100f
        )
    }

    private lateinit var viewModel: GraphViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGraphBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_graph, container, false)
        binding.donutTitle.text = "Income vs Outcome"
        binding.donutChart.donutColors = intArrayOf(
            requireContext().getColor(R.color.cream),
            requireContext().getColor(R.color.gold),
            requireContext().getColor(R.color.green)
        )
        binding.donutChart.animation.duration = 1000
        binding.donutChart.animate(donutSet)
        return binding.root
    }

}