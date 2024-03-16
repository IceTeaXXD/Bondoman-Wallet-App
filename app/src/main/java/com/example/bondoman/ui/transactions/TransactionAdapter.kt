package com.example.bondoman.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bondoman.database.Transaction
import com.example.bondoman.databinding.TransactionItemBinding

class TransactionAdapter(private val data: MutableList<Transaction>,
                         private val fragmentManager: FragmentManager,
                         private val viewModel: TransactionsViewModel,
                         private val onItemClick: (Int) -> Unit
                         ):
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>(),
    ConfirmationModal.ConfirmationDialogListener{
    private var currentItemPosition: Int? = null
    inner class ViewHolder(private val binding: TransactionItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTransaksi.text = transaction.transaction_name
            binding.tvTransactionDate.text = transaction.transaction_date
            binding.tvKategori.text = transaction.transaction_category
            binding.Location.text = transaction.transaction_location
            binding.price.text = transaction.transaction_price.toString()
            binding.btnDelete.setOnClickListener {
                val confirmationDialog = ConfirmationModal()
                currentItemPosition = adapterPosition
                confirmationDialog.setConfirmationDialogListener(this@TransactionAdapter)
                confirmationDialog.show(fragmentManager, "ConfirmationDialog")
            }
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

    override fun onDialogPositiveClick() {
        currentItemPosition?.let { position ->
            val transactionToDelete = data[position]
            viewModel.deleteTransaction(transactionToDelete)
            data.removeAt(position)
            notifyItemRemoved(position)
            currentItemPosition = null
        }
    }
}
