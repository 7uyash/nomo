package com.nomo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nomo.app.data.sync.SyncStatus
import java.util.UUID

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val placeName: String,
    val dishName: String? = null,
    val foodVibe: String? = null, // e.g., "🍕 Tasty", "☕ Cozy", "🌶️ Spicy", "🍰 Sweet", "🍷 Chill"
    val category: String = "Food", // Food, Cafe, Travel, Event, Place, Shopping, Landmark, Personal, Other
    val note: String? = null,
    val tripId: String? = null,
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val driveFileId: String? = null,
    val driveJsonId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

fun MemoryEntity.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim().lowercase()

    // 1. Place / Location Name
    if (placeName.lowercase().contains(q)) return true
    // 2. Memory Title / Dish Name
    if (dishName?.lowercase()?.contains(q) == true) return true
    // 3. Personal Notes & Highlights
    if (note?.lowercase()?.contains(q) == true) return true
    // 4. Category
    if (category.lowercase().contains(q)) return true
    // 5. Food Vibe / Mood
    if (foodVibe?.lowercase()?.contains(q) == true) return true

    // 6. Formatted Date (Month, Day of month, Year, Day of week)
    try {
        val dateStr = java.text.SimpleDateFormat("EEEE MMMM d yyyy MMM", java.util.Locale.getDefault()).format(java.util.Date(timestamp)).lowercase()
        if (dateStr.contains(q)) return true
    } catch (_: Exception) {}

    // 7. Coordinates
    val coordsStr = "%.4f %.4f".format(latitude, longitude)
    if (coordsStr.contains(q)) return true

    return false
}
