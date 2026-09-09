package com.nomo.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
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
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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
import com.nomo.app.ui.trips.AlbumDetailScreen
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
        // ── Location gate state ───────────────────────────────────────────
        var locationPermissionGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            )
        }
        var locationServicesEnabled by remember { mutableStateOf(locationHelper.isLocationEnabled()) }

        // Re-check both whenever the app comes back to the foreground (e.g. after user changes settings)
        LaunchedEffect(Unit) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                locationPermissionGranted = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                locationServicesEnabled = locationHelper.isLocationEnabled()
            }
        }

        val startupPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            locationServicesEnabled = locationHelper.isLocationEnabled()
        }

        // First-time permission request
        LaunchedEffect(Unit) {
            if (!locationPermissionGranted) {
                startupPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        // Show blocking gate screen if permission or services are not available
        if (!locationPermissionGranted || !locationServicesEnabled) {
            LocationGateScreen(
                permissionMissing = !locationPermissionGranted,
                onRequestPermission = {
                    startupPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                onOpenLocationSettings = {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            )
            return
        }

        // ── App state (only reached when location is fully available) ─────
        var currentTab by remember { mutableStateOf("Map") }
        var selectedDetailMemoryId by remember { mutableStateOf<String?>(null) }
        var selectedTripFilterId by remember { mutableStateOf<String?>(null) }
        var selectedAlbumId by remember { mutableStateOf<String?>(null) }

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
        var capturedPlaceName by remember { mutableStateOf("Captured Location") }
        var showCaptureSheet by remember { mutableStateOf(false) }

        // Resurfacing memory states
        var resurfacingMatch by remember { mutableStateOf<Pair<MemoryEntity, Double>?>(null) }
        var onThisDayMemory by remember { mutableStateOf<MemoryEntity?>(null) }
        var userLat by remember { mutableStateOf(0.0) }
        var userLon by remember { mutableStateOf(0.0) }

        // Fetch live location immediately (permission is guaranteed at this point)
        LaunchedEffect(Unit) {
            val loc = locationHelper.getCurrentLocation()
            if (loc != null) {
                userLat = loc.latitude
                userLon = loc.longitude
            }
        }

        // Resurfacing / On-This-Day check (runs whenever memories list changes)
        // Location permission is guaranteed at this point (gate above handles missing permission)
        LaunchedEffect(memories, userLat, userLon) {
            if (userLat != 0.0 && userLon != 0.0) {
                val match = repository.getResurfacingMemoryNear(userLat, userLon)
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
                    val place = locationHelper.getPlaceName(lat, lon)

                    val bitmap = BitmapFactory.decodeFile(currentPhotoFile!!.absolutePath)
                    capturedBitmap = bitmap
                    capturedPhotoPath = currentPhotoFile!!.absolutePath
                    capturedLat = lat
                    capturedLon = lon
                    capturedPlaceName = place
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
                .requestIdToken("306348016041-e6gvvbptth40rfrq869o9q5hi05k6tgr.apps.googleusercontent.com")
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope(DriveScopes.DRIVE_FILE))
                .build()
            val client = GoogleSignIn.getClient(this, gso)
            googleAuthLauncher.launch(client.signInIntent)
        }

        val detailMemory = memories.find { it.id == selectedDetailMemoryId }

        Scaffold(
            bottomBar = {
                if (selectedDetailMemoryId == null && selectedAlbumId == null) {
                    NomoBottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = {
                            selectedAlbumId = null
                            currentTab = it
                        },
                        onCaptureClick = { requestPermissionsAndCapture() }
                    )
                }
            },
            containerColor = NomoCream
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                val albumTrip = if (selectedAlbumId != null) trips.find { it.id == selectedAlbumId } else null
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
                } else if (albumTrip != null) {
                    val albumMemories = memories.filter { it.tripId == albumTrip.id }
                    AlbumDetailScreen(
                        trip = albumTrip,
                        memories = albumMemories,
                        onBackClick = { selectedAlbumId = null },
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
                            onProfileSyncClick = { currentTab = "Settings" },
                            userLat = userLat,
                            userLon = userLon
                        )

                        "Timeline", "Memories" -> TimelineScreen(
                            memories = memories,
                            onMemoryClick = { id -> selectedDetailMemoryId = id }
                        )

                        "Albums", "Food" -> TripsScreen(
                            trips = trips,
                            memories = memories,
                            onTripClick = { tripId ->
                                selectedAlbumId = tripId
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
                        initialPlaceName = capturedPlaceName,
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
                                Toast.makeText(applicationContext, "Memory saved!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCreateAlbum = { albumName ->
                            lifecycleScope.launch {
                                repository.createTrip(albumName, "", null)
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

            // Timeline tab
            NavItem(
                label = "Timeline",
                icon = Icons.Default.Timeline,
                isSelected = currentTab == "Timeline" || currentTab == "Memories",
                onClick = { onTabSelected("Timeline") }
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
                isSelected = currentTab == "Food" || currentTab == "Albums",
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
            tint = if (isSelected) NomoSage else NomoDeepCharcoal.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) NomoSage else NomoDeepCharcoal.copy(alpha = 0.6f)
        )
    }
}

/**
 * Full-screen blocking gate shown when location permission is denied
 * or device location services are disabled.
 */
@Composable
private fun LocationGateScreen(
    permissionMissing: Boolean,
    onRequestPermission: () -> Unit,
    onOpenLocationSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1207),
                        Color(0xFF2D1F0A),
                        Color(0xFF3D2B12)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Icon badge
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFFFFC105).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFFFFC105).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📍", fontSize = 38.sp)
                }
            }

            // Title
            Text(
                text = if (permissionMissing) "Location Access Needed" else "Turn On Location",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC105),
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (permissionMissing) {
                    "NOMO needs access to your location to show your food memories on the map and help you rediscover nearby experiences."
                } else {
                    "Your device's location services are turned off. Please enable GPS or Network location so NOMO can place you on the map."
                },
                fontSize = 15.sp,
                color = Color(0xFFFFFDF9).copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Primary action button
            Button(
                onClick = if (permissionMissing) onRequestPermission else onOpenLocationSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC105),
                    contentColor = Color(0xFF1A1207)
                )
            ) {
                Text(
                    text = if (permissionMissing) "Grant Location Permission" else "Open Location Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Helper hint
            Text(
                text = if (permissionMissing) {
                    "You will be asked to allow location access. Please tap \"Allow\" to continue."
                } else {
                    "After enabling location, come back to the app — it will open automatically."
                },
                fontSize = 12.sp,
                color = Color(0xFFFFFDF9).copy(alpha = 0.45f),
                textAlign = TextAlign.Center
            )
        }
    }
}
