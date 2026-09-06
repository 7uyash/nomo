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
                // 1. Try GPS last known (within 10 mins)
                val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLocation != null && (System.currentTimeMillis() - gpsLocation.time) < 600_000) {
                    continuation.resume(gpsLocation)
                    return@suspendCancellableCoroutine
                }

                // 2. Try Network last known (within 10 mins)
                val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null && (System.currentTimeMillis() - networkLocation.time) < 600_000) {
                    continuation.resume(networkLocation)
                    return@suspendCancellableCoroutine
                }

                // 3. Fall back to active location listener
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

                val provider = when {
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                    else -> null
                }

                if (provider != null) {
                    locationManager.requestLocationUpdates(provider, 0L, 0f, listener)
                } else {
                    // Try getting any stale last location regardless of age before fallback
                    val anyGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val anyNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val bestAny = anyGps ?: anyNet
                    if (bestAny != null) {
                        continuation.resume(bestAny)
                    } else {
                        val defaultLoc = Location("default").apply {
                            latitude = 28.6139
                            longitude = 77.2090
                        }
                        continuation.resume(defaultLoc)
                    }
                }

                continuation.invokeOnCancellation {
                    locationManager.removeUpdates(listener)
                }
            } catch (e: Exception) {
                val anyGps = try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Exception) { null }
                val anyNet = try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { null }
                val bestAny = anyGps ?: anyNet
                val fallbackLoc = bestAny ?: Location("fallback").apply {
                    latitude = 28.6139
                    longitude = 77.2090
                }
                if (continuation.isActive) {
                    continuation.resume(fallbackLoc)
                }
            }
        }
    }
}
