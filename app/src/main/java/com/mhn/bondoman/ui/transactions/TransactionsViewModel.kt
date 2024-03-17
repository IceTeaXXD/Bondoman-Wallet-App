package com.mhn.bondoman.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.mhn.bondoman.BondomanApp
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.database.TransactionSummary
import com.mhn.bondoman.repository.AppRepository
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repository: AppRepository) : ViewModel() {
    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction

    // Store all of transaction
    private val _allTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions

    private val _incomeSummary = MutableLiveData<List<TransactionSummary>>()
    val incomeSumamry: LiveData<List<TransactionSummary>> = _incomeSummary

    private val _outcomeSummary = MutableLiveData<List<TransactionSummary>>()
    val outcomeSummary: LiveData<List<TransactionSummary>> = _outcomeSummary
    fun getTransactionById(transactionId: Int) {
        viewModelScope.launch {
            val result = repository.getTransactionById(transactionId)
            _transaction.postValue(result)
        }
    }

    fun getTransactionByEmail(email: String) {
        viewModelScope.launch {
            val result = repository.getTransactionsByEmail(email)
            _allTransactions.postValue(result)
        }
    }

    fun getLast7Transaction(email: String, category: String) {
        viewModelScope.launch {
            val result = repository.getLast7Transaction(email, category)
            if (category == "Income") {
                _incomeSummary.postValue(result)
            } else {
                _outcomeSummary.postValue(result)
            }
        }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(updatedTransaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
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