package com.mhn.bondoman.ui.graph

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mhn.bondoman.R
import com.mhn.bondoman.databinding.FragmentGraphBinding

class GraphFragment : Fragment() {

    companion object {
        fun newInstance() = GraphFragment()
        private val donutSet = listOf(
            20f,
            80f,
            100f
        )
        private val horizontalBarSet = listOf(
            "PORRO" to 5F,
            "FUSCE" to 6.4F,
            "EGET" to 3F
        )

    }

    private lateinit var viewModel: GraphViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGraphBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_graph, container, false)
        binding.donutTitle.text = "Income vs Outcome"
        binding.donutChart.donutColors = intArrayOf(
            requireContext().getColor(R.color.neon_yellow),
            requireContext().getColor(R.color.neon_red),
            requireContext().getColor(R.color.white)
        )
        binding.donutChart.animation.duration = 1000
        binding.donutChart.animate(donutSet)
        binding.barChartHorizontal.barsColor = requireContext().getColor(R.color.neon_yellow)
        binding.barChartHorizontal.labelsColor = requireContext().getColor(R.color.white)
        binding.barChartHorizontal.animation.duration = 1000
        binding.barChartHorizontal.animate(horizontalBarSet)
        val category = resources.getStringArray(R.array.income_outcome_options)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            category
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
        binding.categorySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        return binding.root
    }

}