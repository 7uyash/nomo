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
