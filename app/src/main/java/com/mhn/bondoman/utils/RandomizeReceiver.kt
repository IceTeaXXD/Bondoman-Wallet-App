package com.mhn.bondoman.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class RandomizeReceiver: BroadcastReceiver() {

    companion object {
        @Volatile
        private var INSTANCE: RandomizeReceiver? = null

        fun getInstance(): RandomizeReceiver{
            if(INSTANCE == null){
                INSTANCE = RandomizeReceiver()
            }
            return INSTANCE!!
        }
    }

    private val titles: MutableList<String> = mutableListOf()
    private var _selectedTitle = ""
    val selectedTitle get() = _selectedTitle
    init {
        titles.apply {
            add("Food")
            add("Drink")
            add("Apparel")
            add("Candi Prambanan")
            add("Sepuh WBD")
        }
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val selectedTitle = titles.random()
        _selectedTitle = selectedTitle
    }
}