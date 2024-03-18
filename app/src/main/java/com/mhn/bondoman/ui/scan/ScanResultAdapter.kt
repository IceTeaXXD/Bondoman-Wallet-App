package com.mhn.bondoman.ui.scan

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhn.bondoman.databinding.ScanResponseItemBinding

class ScanResultAdapter(private val activity: Activity,
                        private val data: MutableList<ScanResultData>) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: ScanResponseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(transaction: ScanResultData){
                binding.tvTransaksi.text = transaction.title
                binding.price.text = transaction.price.toString()
                binding.qty.text = transaction.qty.toString()
            }
        }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScanResultAdapter.ViewHolder {
        return ViewHolder(ScanResponseItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
            )
        )
    }

    override fun onBindViewHolder(holder: ScanResultAdapter.ViewHolder, position: Int) {
        val transaction = data[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int {
        return data.count()
    }
}