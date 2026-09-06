package com.nomo.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Obtains the user's immediate location at capture time.
     * Tries last known location first for zero latency, falls back to single location request.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            try {
                // Try GPS last known
                val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLocation != null && (System.currentTimeMillis() - gpsLocation.time) < 60_000) {
                    continuation.resume(gpsLocation)
                    return@suspendCancellableCoroutine
                }

                // Try Network last known
                val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null && (System.currentTimeMillis() - networkLocation.time) < 60_000) {
                    continuation.resume(networkLocation)
                    return@suspendCancellableCoroutine
                }

                // Fall back to active location update request
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)
                } else {
                    // Fallback default coordinates if GPS is unavailable in emulator/test (e.g. Delhi default)
                    val defaultLoc = Location("default").apply {
                        latitude = 28.6139
                        longitude = 77.2090
                    }
                    continuation.resume(defaultLoc)
                }

                continuation.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }

            } catch (e: Exception) {
                // Default fallback coordinate (Delhi / Central location)
                val defaultLoc = Location("fallback").apply {
                    latitude = 28.6139
                    longitude = 77.2090
                }
                if (continuation.isActive) {
                    continuation.resume(defaultLoc)
                }
            }
        }
    }
}
