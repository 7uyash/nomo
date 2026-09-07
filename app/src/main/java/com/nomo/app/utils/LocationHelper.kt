package com.nomo.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Returns the best available location within a 10-second window.
     * Priority: GPS cached (10 min) → Network cached (10 min) → live GPS fix → live Network fix → stale cached → null
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        // 1. Return any cached GPS fix within the last 10 minutes
        val gpsCached = try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (_: Exception) { null }

        if (gpsCached != null && (System.currentTimeMillis() - gpsCached.time) < 600_000) {
            return gpsCached
        }

        // 2. Return any cached Network fix within the last 10 minutes
        val netCached = try {
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) { null }

        if (netCached != null && (System.currentTimeMillis() - netCached.time) < 600_000) {
            return netCached
        }

        // 3. Request a live fix with a 10-second timeout so we never hang
        val liveLocation = withTimeoutOrNull(10_000L) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try { locationManager.removeUpdates(this) } catch (_: Exception) {}
                        if (continuation.isActive) continuation.resume(location)
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
                }

                val provider = when {
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                        LocationManager.GPS_PROVIDER
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                        LocationManager.NETWORK_PROVIDER
                    else -> null
                }

                if (provider != null) {
                    try {
                        locationManager.requestLocationUpdates(provider, 0L, 0f, listener)
                        continuation.invokeOnCancellation {
                            try { locationManager.removeUpdates(listener) } catch (_: Exception) {}
                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) continuation.resume(null as Location?)
                    }
                } else {
                    // No provider enabled — resolve immediately with null
                    if (continuation.isActive) continuation.resume(null as Location?)
                }
            }
        }

        if (liveLocation != null) return liveLocation

        // 4. Last resort: return any stale cached location regardless of age
        return gpsCached ?: netCached
    }

    /**
     * Reverse-geocodes latitude and longitude into a user-friendly place name.
     */
    fun getPlaceName(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val feature = address.featureName ?: address.locality ?: address.subAdminArea
                val locality = address.locality
                if (feature != null && locality != null && feature != locality) {
                    "$feature, $locality"
                } else {
                    feature ?: locality ?: "Captured Location"
                }
            } else {
                "Captured Location"
            }
        } catch (e: Exception) {
            "Captured Location"
        }
    }
}
