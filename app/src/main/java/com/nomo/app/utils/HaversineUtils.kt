package com.nomo.app.utils

import com.nomo.app.data.local.MemoryEntity
import kotlin.math.*

object HaversineUtils {
    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * Calculates distance between two GPS coordinates in meters.
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Finds nearby memories within [radiusMeters] from current user location.
     * Excludes memories created within [minAgeMillis] (e.g., last 1 hour) so immediate photos aren't resurfaced as throwbacks.
     */
    fun findNearbyMemories(
        currentLat: Double,
        currentLon: Double,
        memories: List<MemoryEntity>,
        radiusMeters: Double = 300.0,
        minAgeMillis: Long = 3600_000L // 1 hour threshold
    ): List<Pair<MemoryEntity, Double>> {
        val now = System.currentTimeMillis()
        return memories
            .filter { now - it.timestamp >= minAgeMillis }
            .map { memory ->
                val dist = calculateDistanceMeters(currentLat, currentLon, memory.latitude, memory.longitude)
                memory to dist
            }
            .filter { it.second <= radiusMeters }
            .sortedBy { it.second } // Nearest first
    }
}
