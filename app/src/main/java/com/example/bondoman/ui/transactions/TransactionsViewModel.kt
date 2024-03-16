package com.example.bondoman.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
}

class TransactionsViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}