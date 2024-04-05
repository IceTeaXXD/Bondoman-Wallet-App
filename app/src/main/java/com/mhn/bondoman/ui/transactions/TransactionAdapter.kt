package com.mhn.bondoman.ui.transactions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.databinding.TransactionItemBinding
import com.mhn.bondoman.utils.LocationAdapter

class TransactionAdapter(
    private val activity: Activity,
    private val data: MutableList<Transaction>,
    private val fragmentManager: FragmentManager,
    private val viewModel: TransactionsViewModel,
    private val onItemClick: (Int) -> Unit
) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>(),
    ConfirmationModal.ConfirmationDialogListener {
    private var currentItemPosition: Int? = null

    inner class ViewHolder(private val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val locationService = LocationAdapter.getInstance(activity)
        fun bind(transaction: Transaction) {
            binding.tvTransaksi.text = transaction.transaction_name
            binding.tvTransactionDate.text = transaction.transaction_date
            binding.Location.text = transaction.transaction_location
            binding.Location.contentDescription = "View location of ${transaction.transaction_name}"
            if (transaction.transaction_category == "Income") {
                binding.price.text = "+ Rp ${transaction.transaction_price}"
            } else {
                binding.price.text = "- Rp ${transaction.transaction_price}"
            }
            binding.price.setTextColor(
                if (transaction.transaction_category == "Income") {
                    activity.getColor(android.R.color.holo_green_light)
                } else {
                    activity.getColor(android.R.color.holo_red_light)
                }
            )
            binding.btnDelete.setOnClickListener {
                val confirmationDialog = ConfirmationModal()
                currentItemPosition = adapterPosition
                confirmationDialog.setConfirmationDialogListener(this@TransactionAdapter)
                confirmationDialog.show(fragmentManager, "ConfirmationDialog")
            }
            binding.btnDelete.contentDescription = "Delete ${transaction.transaction_name}"
            itemView.setOnClickListener {
                onItemClick(transaction.transaction_id ?: 0)
            }
            binding.locationSymbol.contentDescription =
                "View location symbol of ${transaction.transaction_name}"
            binding.locationSymbol.setOnClickListener {
                val gmmIntentUri =
                    Uri.parse("geo:0,0?q=${transaction.transaction_latitude},${transaction.transaction_longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                activity.startActivity(mapIntent)
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
