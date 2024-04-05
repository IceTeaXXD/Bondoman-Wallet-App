package com.mhn.bondoman.ui.graph

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mhn.bondoman.R
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.TransactionSummary
import com.mhn.bondoman.databinding.FragmentGraphBinding
import com.mhn.bondoman.ui.transactions.TransactionsViewModel

class GraphFragment : Fragment() {
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var lineChart: LineChart
    private lateinit var categorySpinner: Spinner
    private var countIncome = 0.0f
    private var countOutcome = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            TransactionsViewModel.FACTORY
        )[TransactionsViewModel::class.java]
        val email = KeyStoreManager.getInstance(requireContext()).getEmail() as String
        viewModel.getTransactionByEmail(email)
        viewModel.getLast7Transaction(email, "Income")
        viewModel.getLast7Transaction(email, "Outcome")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        categorySpinner = binding.etKategori!!
        lineChart = binding.lineChart!!
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.description.isEnabled = true
        val description = Description()

        description.text = "Day"
        description.textSize = 15f

        configurePieChart(binding.pieChart!!)
        // Try to get data
        val category = resources.getStringArray(R.array.income_outcome_options)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            category
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        viewModel.incomeSumamry.observe(viewLifecycleOwner, Observer { transactionsSummary ->
            transactionsSummary.let {
                for (transactionSummary in transactionsSummary) {
                    Log.d("TransactionSummary", transactionSummary.toString())
                }
                updateLineChart(viewModel.incomeSumamry.value ?: emptyList(), "Income")
                categorySpinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedCategory = parent.getItemAtPosition(position).toString()
                        if (selectedCategory == "Income") {
                            Log.d(
                                "UpdateLineChartSend(INCOME)",
                                viewModel.incomeSumamry.value.toString()
                            )
                            updateLineChart(
                                viewModel.incomeSumamry.value ?: emptyList(),
                                selectedCategory
                            )

                        } else {
                            Log.d(
                                "UpdateLineChartSend(OUTCOME)",
                                viewModel.outcomeSummary.value.toString()
                            )
                            updateLineChart(
                                viewModel.outcomeSummary.value ?: emptyList(),
                                selectedCategory
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }
            }
        })
        viewModel.outcomeSummary.observe(viewLifecycleOwner, Observer { transactionsSummary ->
            transactionsSummary.let {
                for (transactionSummary in transactionsSummary) {
                    Log.d("TransactionSummary2", transactionSummary.toString())
                }
            }
        })
        viewModel.allTransactions.observe(viewLifecycleOwner, Observer { transactions ->
            transactions?.let {
                for (transaction in transactions) {
                    Log.d("Transactions", transaction.toString())
                    if (transaction.transaction_category == "Income") {
                        countIncome += transaction.transaction_price
                    } else {
                        countOutcome += transaction.transaction_price
                    }
                }
                updatePieChartData(binding.pieChart!!, countIncome, countOutcome)
            }
        })
        Log.d("YourTag3", "Animate")
        return binding.root
    }

    private fun configurePieChart(chart: PieChart) {
        chart.description.isEnabled = false
        chart.isDrawHoleEnabled = true
        chart.holeRadius = 50f
        chart.transparentCircleRadius = 0f
        chart.setHoleColor(Color.WHITE)
        chart.legend.isEnabled = false

        chart.setDrawCenterText(true)
        chart.setCenterTextSize(16f)
        chart.setCenterTextColor(Color.GRAY)

        val legend = chart.legend
        legend.isEnabled = false
        chart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun updatePieChartData(chart: PieChart, countIncome: Float, countOutcome: Float) {
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(countIncome, "Income"))
            add(PieEntry(countOutcome, "Outcome"))
        }

        val dataSet = PieDataSet(entries, "Income vs Outcome")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.neon_red),
            ContextCompat.getColor(requireContext(), R.color.neon_yellow)
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f
        val difference = countIncome - countOutcome
        chart.centerText = "CashFlow\n%.2f".format(difference)
        val data = PieData(dataSet)
        chart.data = data
        chart.invalidate()
    }

    private fun updateLineChart(transactions: List<TransactionSummary>, category: String) {
        val labels = mutableListOf<String>()
        for (transaction in transactions) {
            Log.d("UpdateLineChart", category + transaction.transaction_date)
            labels.add(transaction.transaction_date)
        }
        val datas = mutableListOf<Entry>()
        var n = 1
        for (transaction in transactions) {
            Log.d("UpdateLineChart", category + transaction.total_price)
            datas.add(Entry(n.toFloat(), transaction.total_price))
            n++
        }
        val xAxis = lineChart.xAxis
        lineChart.setDrawBorders(false)
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.white)

        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.textColor = ContextCompat.getColor(requireContext(), R.color.white)
//        leftAxis.isEnabled = false

        val rightAxis = lineChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.isEnabled = false

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        xAxis.isEnabled = true
        xAxis.labelCount = labels.size
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val lineData = LineDataSet(datas, category)
        lineData.color = ContextCompat.getColor(requireContext(), R.color.neon_yellow)
        lineData.lineWidth = 3f
        lineData.setDrawFilled(true)
        lineData.valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        lineData.valueTextSize = 0f
        lineData.setCircleColor(Color.WHITE)
        lineData.isDrawCircleHoleEnabled
        lineData.fillColor = ContextCompat.getColor(requireContext(), R.color.neon_yellow)
        val data = LineData(lineData)
        lineChart.data = data
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false

        lineChart.invalidate()
    }
}