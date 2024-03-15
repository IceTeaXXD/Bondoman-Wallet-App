package com.example.bondoman.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.database.Transaction
import com.example.bondoman.databinding.TransactionItemBinding

class TransactionAdapter(private val data: List<Transaction>):
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    class ViewHolder(binding: TransactionItemBinding): RecyclerView.ViewHolder(binding.root) {
        val transaction_name = binding.tvTransaksi
        val transaction_date = binding.tvTransactionDate
        val transaction_category = binding.tvKategori
        val transaction_price = binding.price
        val transaction_location = binding.Location
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TransactionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.transaction_name.text = data[position].transaction_name.toString()
        holder.transaction_date.text = data[position].transaction_date.toString()
        holder.transaction_category.text = data[position].transaction_category.toString()
        holder.transaction_location.text = data[position].transaction_location.toString()
        holder.transaction_price.text = data[position].transaction_price.toString()
    }

    override fun getItemCount(): Int {
        return data.count()
    }
}