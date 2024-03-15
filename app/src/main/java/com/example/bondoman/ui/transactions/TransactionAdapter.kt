package com.example.bondoman.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.database.Transaction
import com.example.bondoman.databinding.TransactionItemBinding

class TransactionAdapter(private val data: List<Transaction>,
                         private val onItemClick: (Int) -> Unit):
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>()  {
    inner class ViewHolder(private val binding: TransactionItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTransaksi.text = transaction.transaction_name
            binding.tvTransactionDate.text = transaction.transaction_date
            binding.tvKategori.text = transaction.transaction_category
            binding.Location.text = transaction.transaction_location
            binding.price.text = transaction.transaction_price.toString()

            itemView.setOnClickListener {
                onItemClick(transaction.transaction_id ?: 0)
            }
        }
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
        val transaction = data[position]
        holder.bind(transaction)
    }
    override fun getItemCount(): Int {
        return data.count()
    }
}
