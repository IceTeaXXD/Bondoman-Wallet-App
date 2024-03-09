package com.example.bondoman.ui.graph

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bondoman.R

class GraphFragment : Fragment() {

    companion object {
        fun newInstance() = GraphFragment()
    }

    private lateinit var viewModel: GraphViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GraphViewModel::class.java)
        // TODO: Use the ViewModel
    }

}