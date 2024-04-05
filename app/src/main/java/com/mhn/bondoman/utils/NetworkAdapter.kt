package com.mhn.bondoman.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkAdapter(val context: Context) {
    interface NetworkListener {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }

    private var subscribers = mutableListOf<NetworkListener>()

    companion object {
        @Volatile
        private var INSTANCE: NetworkAdapter? = null

        fun getInstance(context: Context): NetworkAdapter {
            if (INSTANCE == null) {
                INSTANCE = NetworkAdapter(context)
            }
            return INSTANCE!!
        }
    }

    init {
        val networkRequest: NetworkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                notifyConnected()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val unmetered =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                notifyDisconnected()
            }
        }

        val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun subscribe(listener: NetworkListener) {
        subscribers.add(listener)
    }

    fun unsubscribe(listener: NetworkListener) {
        subscribers.remove(listener)
    }

    private fun notifyConnected() {
        subscribers.forEach {
            it.onNetworkAvailable()
        }
    }

    private fun notifyDisconnected() {
        subscribers.forEach {
            it.onNetworkLost()
        }
    }
}