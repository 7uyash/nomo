package com.nomo.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.work.*
import com.nomo.app.data.local.MemoryDao
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.data.local.NomoDatabase
import com.nomo.app.data.local.TripDao
import com.nomo.app.data.local.TripEntity
import com.nomo.app.data.sync.MemorySyncWorker
import com.nomo.app.data.sync.SyncStatus
import com.nomo.app.utils.HaversineUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MemoryRepository(private val context: Context) {
    private val db = NomoDatabase.getInstance(context)
    private val memoryDao: MemoryDao = db.memoryDao()
    private val tripDao: TripDao = db.tripDao()

    val allMemoriesFlow: Flow<List<MemoryEntity>> = memoryDao.getAllMemoriesFlow()
    val allTripsFlow: Flow<List<TripEntity>> = tripDao.getAllTripsFlow()
    val pendingSyncFlow: Flow<List<MemoryEntity>> = memoryDao.getPendingSyncMemoriesFlow()
    val syncedMemoriesFlow: Flow<List<MemoryEntity>> = memoryDao.getSyncedMemoriesFlow()

    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> {
        return if (category == "All") allMemoriesFlow else memoryDao.getMemoriesByCategoryFlow(category)
    }

    fun getMemoriesByTrip(tripId: String): Flow<List<MemoryEntity>> {
        return memoryDao.getMemoriesByTripFlow(tripId)
    }

    fun getMemoryByIdFlow(id: String): Flow<MemoryEntity?> {
        return memoryDao.getMemoryByIdFlow(id)
    }

    suspend fun getMemoryById(id: String): MemoryEntity? = withContext(Dispatchers.IO) {
        memoryDao.getMemoryById(id)
    }

    suspend fun savePhotoToInternalStorage(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val photosDir = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }
        val photoFile = File(photosDir, "NOMO_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
        FileOutputStream(photoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        photoFile.absolutePath
    }

    suspend fun createAndSaveMemory(
        photoPath: String,
        latitude: Double,
        longitude: Double,
        placeName: String,
        dishName: String?,
        foodVibe: String?,
        category: String,
        note: String?,
        tripId: String?
    ): MemoryEntity = withContext(Dispatchers.IO) {
        val memory = MemoryEntity(
            photoPath = photoPath,
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis(),
            placeName = placeName.ifBlank { "My Special Spot" },
            dishName = dishName?.takeIf { it.isNotBlank() },
            foodVibe = foodVibe,
            category = category,
            note = note?.takeIf { it.isNotBlank() },
            tripId = tripId?.takeIf { it.isNotBlank() },
            syncStatus = SyncStatus.LOCAL_ONLY
        )
        memoryDao.insertMemory(memory)
        scheduleSync()
        memory
    }

    suspend fun updateMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        memoryDao.updateMemory(memory)
        scheduleSync()
    }

    suspend fun deleteMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        // Remove local photo file
        val file = File(memory.photoPath)
        if (file.exists()) file.delete()
        memoryDao.deleteMemory(memory)
    }

    suspend fun createTrip(name: String, description: String?, coverPhotoPath: String?): TripEntity = withContext(Dispatchers.IO) {
        val trip = TripEntity(
            name = name,
            description = description,
            startDate = System.currentTimeMillis(),
            coverPhotoPath = coverPhotoPath
        )
        tripDao.insertTrip(trip)
        trip
    }

    /**
     * Checks if user's current location is close to an existing memory (within [radiusMeters]).
     * Returns the closest matching memory for "You've been here before 👀" resurfacing banner.
     */
    suspend fun getResurfacingMemoryNear(lat: Double, lon: Double, radiusMeters: Double = 300.0): Pair<MemoryEntity, Double>? = withContext(Dispatchers.IO) {
        val allMemories = memoryDao.getAllMemories()
        val candidates = HaversineUtils.findNearbyMemories(lat, lon, allMemories, radiusMeters)
        candidates.firstOrNull()
    }

    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<MemorySyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "nomo_drive_sync_worker",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
