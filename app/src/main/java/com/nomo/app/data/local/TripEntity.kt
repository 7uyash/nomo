package com.nomo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val startDate: Long,
    val endDate: Long? = null,
    val coverPhotoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
