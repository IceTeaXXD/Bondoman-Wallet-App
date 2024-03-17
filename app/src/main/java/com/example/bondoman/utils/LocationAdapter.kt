package com.example.bondoman.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.Locale

class LocationAdapter(val activity: Activity) {
    private var locationManager: LocationManager
    private var locationListener: LocationListener
    private var locationByGps: Location? = null
    private var locationListenerCallback: ((Location) -> Unit)? = null
    private val geocoder = Geocoder(activity.applicationContext, Locale.getDefault())

    companion object {
        @Volatile
        private var INSTANCE: LocationAdapter? = null

        fun getInstance(activity: Activity): LocationAdapter {
            if(INSTANCE == null){
                INSTANCE = LocationAdapter(activity)
            }
            return INSTANCE!!
        }
    }

    init {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps = location
                locationListenerCallback?.invoke(location)
                locationManager.removeUpdates(this)
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if(
            ContextCompat.checkSelfPermission(
                activity.applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            false
        }else{
            true
        }
    }

    private fun requestLocation() {
        checkLocationPermission()

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            10f,
            locationListener
        )

    }

    fun getLocation(callback: (Location) -> Unit) {
        locationListenerCallback = callback
        requestLocation()
    }

    fun transformToReadable(loc: Location): String {
        try {
            val addresses: List<Address> = geocoder.getFromLocation(loc.latitude, loc.longitude, 5)!!

            return addresses[0].locality
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Earth"
    }

    fun transformToCoord(locationName: String): Location? {
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(locationName, 5)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val location = Location("")
                location.latitude = address.latitude
                location.longitude = address.longitude
                return location
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}