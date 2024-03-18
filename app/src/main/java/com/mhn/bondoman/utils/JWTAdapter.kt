package com.mhn.bondoman.utils

import android.content.Context
import android.util.Log
import com.mhn.bondoman.api.BondomanApi
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.models.LoginBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class JWTAdapter(val context: Context) {
    interface JWTListener {
        fun onJWTValid()
        fun onJWTInvalid()
    }

    private var subscribers = mutableListOf<JWTListener>()

    companion object {
        @Volatile
        private var INSTANCE: JWTAdapter? = null

        fun getInstance(context: Context): JWTAdapter {
            if (INSTANCE == null) {
                INSTANCE = JWTAdapter(context)
            }
            return INSTANCE!!
        }
    }

    init {
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            if (isJWTValidated()) {
                notifyJWTValid()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    Log.d("JWTAdapter", "------Refreshing token------")
                    val email = KeyStoreManager.getInstance(context).getEmail()
                    val password = KeyStoreManager.getInstance(context).getPassword()
                    if (email == null || password == null) {
                        notifyJWTInvalid()
                        return@launch
                    }
                    val response = BondomanApi.getInstance().login(
                        LoginBody(
                            email,
                            password
                        )
                    )
                    if (response.code() == 200) {
                        KeyStoreManager.getInstance(context).setToken(response.body()!!.token)
                        val token =
                            BondomanApi.getInstance().token("Bearer ${response.body()!!.token}")
                        if (token.code() == 200) {
                            KeyStoreManager.getInstance(context).setExpiry(token.body()!!.exp)
                            notifyJWTValid()
                        } else {
                            notifyJWTInvalid()
                        }
                    } else {
                        notifyJWTInvalid()
                    }
                }
            }
        }, 0, 30, java.util.concurrent.TimeUnit.SECONDS)
    }

    fun isJWTValidated(): Boolean {
        val expiry = KeyStoreManager.getInstance(context).getExpiry()
        Log.d("JWTAdapter", "Expiry: $expiry")
        Log.d("JWTAdapter", "Current time: ${System.currentTimeMillis()}")
        return expiry > System.currentTimeMillis()
    }

    fun subscribe(listener: JWTListener) {
        subscribers.add(listener)
    }

    fun unsubscribe(listener: JWTListener) {
        subscribers.remove(listener)
    }

    private fun notifyJWTValid() {
        for (subscriber in subscribers) {
            subscriber.onJWTValid()
        }
    }

    private fun notifyJWTInvalid() {
        for (subscriber in subscribers) {
            subscriber.onJWTInvalid()
        }
    }
}