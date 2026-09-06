package com.nomo.app.utils

import com.nomo.app.data.local.MemoryEntity
import java.util.Calendar
import java.util.Date
import kotlin.math.*

object HaversineUtils {
    private const val EARTH_RADIUS_METERS = 6371000.0
    const val DEFAULT_RESURFACE_RADIUS_METERS = 300.0

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
        radiusMeters: Double = DEFAULT_RESURFACE_RADIUS_METERS,
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

    /**
     * Finds memories captured on this same Month & Day in previous years ("On this day" throwback).
     */
    fun findOnThisDayMemories(
        memories: List<MemoryEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): List<MemoryEntity> {
        val todayCal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val currentMonth = todayCal.get(Calendar.MONTH)
        val currentDay = todayCal.get(Calendar.DAY_OF_MONTH)
        val currentYear = todayCal.get(Calendar.YEAR)

        val memCal = Calendar.getInstance()

        return memories.filter { memory ->
            memCal.timeInMillis = memory.timestamp
            val memoryMonth = memCal.get(Calendar.MONTH)
            val memoryDay = memCal.get(Calendar.DAY_OF_MONTH)
            val memoryYear = memCal.get(Calendar.YEAR)

            // Match month and day, but strictly from an earlier year
            memoryMonth == currentMonth && memoryDay == currentDay && memoryYear < currentYear
        }.sortedByDescending { it.timestamp }
    }
}
