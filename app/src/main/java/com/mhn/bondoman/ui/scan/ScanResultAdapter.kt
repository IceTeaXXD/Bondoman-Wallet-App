package com.mhn.bondoman.ui.scan

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhn.bondoman.databinding.ScanResponseItemBinding
import com.mhn.bondoman.models.Item

class ScanResultAdapter(private val activity: Activity,
                        private val data: MutableList<Item>) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>(){
    inner class ViewHolder(private val binding: ScanResponseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(transaction: Item){
                binding.tvTransaksi.text = transaction.name
                binding.price.text = "Price: Rp" + transaction.price.toString()
                binding.qty.text = "Qty: " + transaction.qty.toString()
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