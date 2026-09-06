package com.nomo.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.drive.DriveScopes
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.data.local.TripEntity
import com.nomo.app.data.repository.MemoryRepository
import com.nomo.app.data.sync.SyncStatus
import com.nomo.app.ui.capture.QuickCaptureBottomSheet
import com.nomo.app.ui.detail.MemoryDetailScreen
import com.nomo.app.ui.explore.ExploreScreen
import com.nomo.app.ui.profile.ProfileSyncScreen
import com.nomo.app.ui.theme.NOMOTheme
import com.nomo.app.ui.theme.NomoCream
import com.nomo.app.ui.theme.NomoDeepCharcoal
import com.nomo.app.ui.theme.NomoTerracotta
import com.nomo.app.ui.theme.NomoWarmAmber
import com.nomo.app.ui.theme.Typography
import com.nomo.app.ui.timeline.TimelineScreen
import com.nomo.app.ui.trips.TripsScreen
import com.nomo.app.utils.LocationHelper
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var repository: MemoryRepository
    private lateinit var locationHelper: LocationHelper

    private var currentPhotoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = MemoryRepository(applicationContext)
        locationHelper = LocationHelper(applicationContext)

        setContent {
            NOMOTheme {
                NomoAppMainScreen()
            }
        }
    }

    @Composable
    private fun NomoAppMainScreen() {
        var currentTab by remember { mutableStateOf("Map") }
        var selectedDetailMemoryId by remember { mutableStateOf<String?>(null) }
        var selectedTripFilterId by remember { mutableStateOf<String?>(null) }

        val memories by repository.allMemoriesFlow.collectAsState(initial = emptyList())
        val trips by repository.allTripsFlow.collectAsState(initial = emptyList())
        val pendingSyncList by repository.pendingSyncFlow.collectAsState(initial = emptyList())

        var googleAccountName by remember {
            mutableStateOf(GoogleSignIn.getLastSignedInAccount(applicationContext)?.email)
        }

        // Camera photo capture state
        var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
        var capturedPhotoPath by remember { mutableStateOf<String?>(null) }
        var capturedLat by remember { mutableStateOf(28.6139) }
        var capturedLon by remember { mutableStateOf(77.2090) }
        var showCaptureSheet by remember { mutableStateOf(false) }

        // Resurfacing memory states
        var resurfacingMatch by remember { mutableStateOf<Pair<MemoryEntity, Double>?>(null) }
        var onThisDayMemory by remember { mutableStateOf<MemoryEntity?>(null) }

        // Location check & "On This Day" throwback check
        LaunchedEffect(memories) {
            val loc = locationHelper.getCurrentLocation()
            if (loc != null) {
                val match = repository.getResurfacingMemoryNear(loc.latitude, loc.longitude)
                resurfacingMatch = match
            }
            val otdList = repository.getOnThisDayMemories()
            onThisDayMemory = otdList.firstOrNull()
        }

        // Photo Take Picture Contract Launcher
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && currentPhotoFile != null) {
                lifecycleScope.launch {
                    val loc = locationHelper.getCurrentLocation()
                    val lat = loc?.latitude ?: 28.6139
                    val lon = loc?.longitude ?: 77.2090

                    val bitmap = BitmapFactory.decodeFile(currentPhotoFile!!.absolutePath)
                    capturedBitmap = bitmap
                    capturedPhotoPath = currentPhotoFile!!.absolutePath
                    capturedLat = lat
                    capturedLon = lon
                    showCaptureSheet = true
                }
            }
        }

        // Permission Launcher for Camera & Location
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
            val locGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            if (cameraGranted || locGranted) {
                launchCameraCapture(cameraLauncher)
            } else {
                Toast.makeText(this, "Camera & Location access needed to remember your places", Toast.LENGTH_SHORT).show()
            }
        }

        // Google Sign-In Launcher
        val googleAuthLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(Exception::class.java)
                googleAccountName = account?.email
                Toast.makeText(this, "Signed in as ${account?.email}", Toast.LENGTH_SHORT).show()
                repository.scheduleSync()
            } catch (e: Exception) {
                Toast.makeText(this, "Google Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        fun requestPermissionsAndCapture() {
            val hasCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val hasLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (hasCamera && hasLoc) {
                launchCameraCapture(cameraLauncher)
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        fun launchGoogleSignIn() {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope(DriveScopes.DRIVE_FILE))
                .build()
            val client = GoogleSignIn.getClient(this, gso)
            googleAuthLauncher.launch(client.signInIntent)
        }

        val detailMemory = memories.find { it.id == selectedDetailMemoryId }

        Scaffold(
            bottomBar = {
                if (selectedDetailMemoryId == null) {
                    NomoBottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        onCaptureClick = { requestPermissionsAndCapture() }
                    )
                }
            },
            containerColor = NomoCream
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (detailMemory != null) {
                    val nearbyMemories = memories.filter { it.id != detailMemory.id }.take(5)
                    MemoryDetailScreen(
                        memory = detailMemory,
                        nearbyMemories = nearbyMemories,
                        onBackClick = { selectedDetailMemoryId = null },
                        onDeleteClick = { memToDelete ->
                            lifecycleScope.launch {
                                repository.deleteMemory(memToDelete)
                                selectedDetailMemoryId = null
                            }
                        },
                        onMemoryClick = { id -> selectedDetailMemoryId = id }
                    )
                } else {
                    when (currentTab) {
                        "Map" -> ExploreScreen(
                            memories = memories,
                            pendingSyncCount = pendingSyncList.size,
                            resurfacingMatch = resurfacingMatch,
                            onThisDayMemory = onThisDayMemory,
                            onMemoryClick = { id -> selectedDetailMemoryId = id },
                            onProfileSyncClick = { currentTab = "Settings" }
                        )

                        "Memories" -> TimelineScreen(
                            memories = memories,
                            onMemoryClick = { id -> selectedDetailMemoryId = id }
                        )

                        "Food" -> TripsScreen(
                            trips = trips,
                            memories = memories,
                            onTripClick = { tripId ->
                                selectedTripFilterId = tripId
                                currentTab = "Memories"
                            },
                            onCreateTripClick = { name, desc ->
                                lifecycleScope.launch {
                                    repository.createTrip(name, desc, null)
                                }
                            }
                        )

                        "Settings" -> ProfileSyncScreen(
                            googleAccountName = googleAccountName,
                            memories = memories,
                            onConnectGoogleClick = { launchGoogleSignIn() },
                            onSyncNowClick = { repository.scheduleSync() }
                        )
                    }
                }

                // Quick Capture Bottom Sheet
                if (showCaptureSheet && capturedBitmap != null && capturedPhotoPath != null) {
                    QuickCaptureBottomSheet(
                        photoBitmap = capturedBitmap!!,
                        initialPlaceName = "My Experienced Place",
                        latitude = capturedLat,
                        longitude = capturedLon,
                        availableTrips = trips,
                        onSaveMemory = { dishName, placeName, vibe, category, note, tripId ->
                            lifecycleScope.launch {
                                repository.createAndSaveMemory(
                                    photoPath = capturedPhotoPath!!,
                                    latitude = capturedLat,
                                    longitude = capturedLon,
                                    placeName = placeName,
                                    dishName = dishName,
                                    foodVibe = vibe,
                                    category = category,
                                    note = note,
                                    tripId = tripId
                                )
                                showCaptureSheet = false
                                Toast.makeText(applicationContext, "Memory saved to your map! 🍕📍", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDismiss = { showCaptureSheet = false }
                    )
                }
            }
        }
    }

    private fun launchCameraCapture(launcher: androidx.activity.result.ActivityResultLauncher<Uri>) {
        try {
            val photosDir = File(filesDir, "photos").apply { if (!exists()) mkdirs() }
            val photoFile = File(photosDir, "NOMO_CAP_${System.currentTimeMillis()}.jpg")
            currentPhotoFile = photoFile

            val photoUri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                photoFile
            )
            launcher.launch(photoUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Could not open camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun NomoBottomNavBar(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onCaptureClick: () -> Unit
) {
    Surface(
        color = NomoCream,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Map tab
            NavItem(
                label = "Map",
                icon = Icons.Default.Map,
                isSelected = currentTab == "Map",
                onClick = { onTabSelected("Map") }
            )

            // Memories tab
            NavItem(
                label = "Memories",
                icon = Icons.Default.Timeline,
                isSelected = currentTab == "Memories",
                onClick = { onTabSelected("Memories") }
            )

            // Center + capture FAB (yellow, larger)
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(NomoWarmAmber)
                    .clickable { onCaptureClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Capture Memory",
                    tint = NomoDeepCharcoal,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Food tab
            NavItem(
                label = "Food",
                icon = Icons.Default.Restaurant,
                isSelected = currentTab == "Food",
                onClick = { onTabSelected("Food") }
            )

            // Settings tab
            NavItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentTab == "Settings",
                onClick = { onTabSelected("Settings") }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NomoTerracotta else NomoDeepCharcoal.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NomoTerracotta else NomoDeepCharcoal.copy(alpha = 0.6f)
        )
    }
}
