package com.nomo.app.data.local

import androidx.room.*
import com.nomo.app.data.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemoriesFlow(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    suspend fun getAllMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryById(id: String): MemoryEntity?

    @Query("SELECT * FROM memories WHERE id = :id")
    fun getMemoryByIdFlow(id: String): Flow<MemoryEntity?>

    @Query("SELECT * FROM memories WHERE category = :category ORDER BY timestamp DESC")
    fun getMemoriesByCategoryFlow(category: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getMemoriesByTripFlow(tripId: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE syncStatus IN ('LOCAL_ONLY', 'RETRY_QUEUED', 'QUEUED', 'UPLOAD_FAILED')")
    suspend fun getPendingSyncMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE syncStatus IN ('LOCAL_ONLY', 'RETRY_QUEUED', 'QUEUED', 'UPLOAD_FAILED')")
    fun getPendingSyncMemoriesFlow(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE syncStatus = 'SYNCED'")
    fun getSyncedMemoriesFlow(): Flow<List<MemoryEntity>>

    @Query("""
        SELECT * FROM memories 
        WHERE latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLng AND :maxLng 
        ORDER BY timestamp DESC
    """)
    fun getMemoriesInBoundingBoxFlow(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("UPDATE memories SET syncStatus = :status, driveFileId = :driveFileId, driveJsonId = :driveJsonId, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSyncStatus(
        id: String,
        status: SyncStatus,
        driveFileId: String?,
        driveJsonId: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: String)
}
