package com.nomo.app.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nomo.app.data.local.NomoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemorySyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val db = NomoDatabase.getInstance(context)
    private val memoryDao = db.memoryDao()
    private val driveManager = GoogleDriveManager(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pendingMemories = memoryDao.getPendingSyncMemories()
            if (pendingMemories.isEmpty()) {
                return@withContext Result.success()
            }

            val account = driveManager.getSignedInAccount()
            if (account == null) {
                // Mark memories as RETRY_QUEUED if user is offline or not signed in
                pendingMemories.forEach { memory ->
                    if (memory.syncStatus != SyncStatus.RETRY_QUEUED) {
                        memoryDao.updateSyncStatus(
                            id = memory.id,
                            status = SyncStatus.RETRY_QUEUED,
                            driveFileId = memory.driveFileId,
                            driveJsonId = memory.driveJsonId
                        )
                    }
                }
                return@withContext Result.retry()
            }

            var hasFailures = false

            for (memory in pendingMemories) {
                // 1. Set to QUEUED -> UPLOADING state
                memoryDao.updateSyncStatus(
                    id = memory.id,
                    status = SyncStatus.UPLOADING,
                    driveFileId = memory.driveFileId,
                    driveJsonId = memory.driveJsonId
                )

                // 2. Upload to Google Drive
                val (photoDriveId, jsonDriveId) = driveManager.syncMemoryToDrive(memory)

                if (photoDriveId != null || jsonDriveId != null) {
                    // 3. Mark SYNCED
                    memoryDao.updateSyncStatus(
                        id = memory.id,
                        status = SyncStatus.SYNCED,
                        driveFileId = photoDriveId ?: memory.driveFileId,
                        driveJsonId = jsonDriveId ?: memory.driveJsonId
                    )
                } else {
                    // 4. Mark UPLOAD_FAILED / RETRY_QUEUED
                    memoryDao.updateSyncStatus(
                        id = memory.id,
                        status = SyncStatus.UPLOAD_FAILED,
                        driveFileId = memory.driveFileId,
                        driveJsonId = memory.driveJsonId
                    )
                    hasFailures = true
                }
            }

            if (hasFailures) {
                Result.retry()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
