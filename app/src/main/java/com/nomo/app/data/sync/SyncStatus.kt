package com.nomo.app.data.sync

enum class SyncStatus {
    LOCAL_ONLY,
    QUEUED,
    UPLOADING,
    SYNCED,
    UPLOAD_FAILED,
    RETRY_QUEUED
}
