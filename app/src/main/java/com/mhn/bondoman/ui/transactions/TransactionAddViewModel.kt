package com.mhn.bondoman.ui.transactions

import androidx.lifecycle.ViewModel

class TransactionAddViewModel: ViewModel() {

    private var title: String = ""

    fun setTitle(title: String) {
        this@TransactionAddViewModel.title = title
    }

    fun getTitle() : String {
        return title
    }
}