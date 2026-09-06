package com.nomo.app.data.sync

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.nomo.app.data.local.MemoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class GoogleDriveManager(private val context: Context) {

    private var rootFolderId: String? = null
    private var photosFolderId: String? = null
    private var memoriesFolderId: String? = null

    fun getSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    private fun getDriveService(): Drive? {
        val account = getSignedInAccount() ?: return null
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("NOMO Personal Memory Map").build()
    }

    suspend fun initializeFolders(): Boolean = withContext(Dispatchers.IO) {
        val drive = getDriveService() ?: return@withContext false
        try {
            rootFolderId = findOrCreateFolder(drive, "NOMO_Memories", null)
            if (rootFolderId != null) {
                photosFolderId = findOrCreateFolder(drive, "Photos", rootFolderId)
                memoriesFolderId = findOrCreateFolder(drive, "Memories", rootFolderId)
            }
            rootFolderId != null && photosFolderId != null && memoriesFolderId != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun findOrCreateFolder(drive: Drive, folderName: String, parentId: String?): String? {
        val query = if (parentId == null) {
            "mimeType = 'application/vnd.google-apps.folder' and name = '$folderName' and 'root' in parents and trashed = false"
        } else {
            "mimeType = 'application/vnd.google-apps.folder' and name = '$folderName' and '$parentId' in parents and trashed = false"
        }

        val result = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        if (result.files.isNotEmpty()) {
            return result.files[0].id
        }

        val folderMetadata = com.google.api.services.drive.model.File().apply {
            name = folderName
            mimeType = "application/vnd.google-apps.folder"
            if (parentId != null) {
                parents = listOf(parentId)
            }
        }

        val folder = drive.files().create(folderMetadata)
            .setFields("id")
            .execute()

        return folder.id
    }

    /**
     * Checks if a file with [fileName] already exists inside [folderId] on Google Drive.
     * Prevents duplicate uploads on network retry (idempotency guarantee).
     */
    private fun findExistingFileId(drive: Drive, fileName: String, folderId: String): String? {
        val query = "name = '$fileName' and '$folderId' in parents and trashed = false"
        val result = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        return result.files.firstOrNull()?.id
    }

    suspend fun syncMemoryToDrive(memory: MemoryEntity): Pair<String?, String?> = withContext(Dispatchers.IO) {
        val drive = getDriveService() ?: return@withContext null to null
        try {
            if (rootFolderId == null || photosFolderId == null || memoriesFolderId == null) {
                val initialized = initializeFolders()
                if (!initialized) return@withContext null to null
            }

            val photoFileName = "${memory.id}.jpg"
            val jsonFileName = "${memory.id}.json"

            // 1. Check for existing uploaded photo (Idempotency check)
            var uploadedPhotoId = memory.driveFileId
            if (uploadedPhotoId == null) {
                uploadedPhotoId = findExistingFileId(drive, photoFileName, photosFolderId!!)
            }

            // If not found on Drive and local photo file exists, upload it
            if (uploadedPhotoId == null) {
                val localPhotoFile = File(memory.photoPath)
                if (localPhotoFile.exists()) {
                    val mediaContent = FileContent("image/jpeg", localPhotoFile)
                    val photoMetadata = com.google.api.services.drive.model.File().apply {
                        name = photoFileName
                        parents = listOf(photosFolderId)
                    }
                    val photoFile = drive.files().create(photoMetadata, mediaContent)
                        .setFields("id")
                        .execute()
                    uploadedPhotoId = photoFile.id
                }
            }

            // 2. Check for existing uploaded memory JSON manifest (Idempotency check)
            var uploadedJsonId = memory.driveJsonId
            if (uploadedJsonId == null) {
                uploadedJsonId = findExistingFileId(drive, jsonFileName, memoriesFolderId!!)
            }

            if (uploadedJsonId == null) {
                val jsonObject = JSONObject().apply {
                    put("id", memory.id)
                    put("latitude", memory.latitude)
                    put("longitude", memory.longitude)
                    put("timestamp", memory.timestamp)
                    put("placeName", memory.placeName)
                    put("dishName", memory.dishName ?: "")
                    put("foodVibe", memory.foodVibe ?: "")
                    put("category", memory.category)
                    put("note", memory.note ?: "")
                    put("tripId", memory.tripId ?: "")
                    put("photoDriveFileId", uploadedPhotoId ?: "")
                }

                val tempJsonFile = File(context.cacheDir, jsonFileName).apply {
                    writeText(jsonObject.toString(2))
                }

                val jsonContent = FileContent("application/json", tempJsonFile)
                val jsonMetadata = com.google.api.services.drive.model.File().apply {
                    name = jsonFileName
                    parents = listOf(memoriesFolderId)
                }

                val jsonFile = drive.files().create(jsonMetadata, jsonContent)
                    .setFields("id")
                    .execute()

                uploadedJsonId = jsonFile.id
                tempJsonFile.delete()
            }

            Pair(uploadedPhotoId, uploadedJsonId)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }
}
