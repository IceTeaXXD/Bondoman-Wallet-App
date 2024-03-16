package com.example.bondoman.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bondoman.BondomanApp
import com.example.bondoman.database.Transaction
import com.example.bondoman.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction

    fun getTransactionById(transactionId: Int) {
        viewModelScope.launch {
            val result = repository.getTransactionById(transactionId)
            _transaction.postValue(result)
        }
    }
    fun updateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(updatedTransaction)
        }
    }
    companion object {
        @Suppress("UNCHECKED_CAST")
        val FACTORY = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as BondomanApp
                val repository = application.getRepository()
                return TransactionsViewModel(repository) as T
            }
        }
    }
}