package com.nomo.app.ui.explore

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.nomo.app.data.local.MemoryEntity
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File

@Composable
fun NomoMapView(
    memories: List<MemoryEntity>,
    selectedMemoryId: String?,
    onMemorySelect: (MemoryEntity) -> Unit,
    modifier: Modifier = Modifier,
    userLat: Double = 0.0,
    userLon: Double = 0.0,
    centerOnUser: Boolean = false,
    onCenterConsumed: () -> Unit = {},
    zoomLevel: Double = 14.0
) {
    val context = LocalContext.current

    // Build the OSMDroid MapView once
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)
            controller.setZoom(zoomLevel)
        }
    }

    // "My Location" blue dot overlay (uses device GPS/network internally)
    val myLocationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableMyLocation()
        }
    }

    // Initial center: use live location if available, else first memory, else world center
    LaunchedEffect(Unit) {
        mapView.overlays.add(myLocationOverlay)
        val initialCenter = when {
            userLat != 0.0 && userLon != 0.0 -> GeoPoint(userLat, userLon)
            memories.isNotEmpty() -> GeoPoint(memories.first().latitude, memories.first().longitude)
            else -> GeoPoint(20.5937, 78.9629) // India center as safe default
        }
        mapView.controller.setCenter(initialCenter)
    }

    // Re-center on user whenever centerOnUser is triggered
    LaunchedEffect(centerOnUser) {
        if (centerOnUser && userLat != 0.0 && userLon != 0.0) {
            mapView.controller.animateTo(GeoPoint(userLat, userLon))
            onCenterConsumed()
        }
    }

    // Redraw memory markers whenever memories or selection changes
    LaunchedEffect(memories, selectedMemoryId) {
        // Keep myLocationOverlay; clear only marker overlays
        mapView.overlays.removeIf { it is Marker }

        memories.forEach { memory ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(memory.latitude, memory.longitude)
                title = memory.dishName ?: memory.placeName
                snippet = memory.placeName
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                val iconBitmap = createCustomMarkerBitmap(context, memory, isSelected = memory.id == selectedMemoryId)
                icon = BitmapDrawable(context.resources, iconBitmap)

                setOnMarkerClickListener { _, _ ->
                    onMemorySelect(memory)
                    mapView.controller.animateTo(GeoPoint(memory.latitude, memory.longitude))
                    true
                }
            }
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )

    // Lifecycle: start/stop location updates
    DisposableEffect(Unit) {
        myLocationOverlay.enableMyLocation()
        onDispose {
            myLocationOverlay.disableMyLocation()
        }
    }
}

/**
 * Creates a playful photo marker bitmap with rounded photo thumbnail & food vibe emoji badge.
 * Selected markers get a brighter amber ring to stand out.
 */
private fun createCustomMarkerBitmap(
    context: Context,
    memory: MemoryEntity,
    isSelected: Boolean = false
): Bitmap {
    val size = 110
    val bitmap = Bitmap.createBitmap(size, size + 20, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Outer border — amber when selected, terracotta otherwise
    paint.color = if (isSelected) 0xFFFFC105.toInt() else 0xFFD96B43.toInt()
    val outerRect = RectF(5f, 5f, size.toFloat() - 5f, size.toFloat() - 5f)
    canvas.drawRoundRect(outerRect, 24f, 24f, paint)

    // Inner photo
    val innerFile = File(memory.photoPath)
    val photoBitmap = if (innerFile.exists()) {
        try { BitmapFactory.decodeFile(memory.photoPath) } catch (e: Exception) { null }
    } else null

    if (photoBitmap != null) {
        val scaledPhoto = Bitmap.createScaledBitmap(photoBitmap, size - 20, size - 20, false)
        canvas.drawBitmap(scaledPhoto, 10f, 10f, null)
    } else {
        // Fallback flat amber fill
        paint.color = 0xFFFFC105.toInt()
        val innerRect = RectF(10f, 10f, size.toFloat() - 10f, size.toFloat() - 10f)
        canvas.drawRoundRect(innerRect, 20f, 20f, paint)
    }

    // Pointer pin at bottom
    paint.color = if (isSelected) 0xFFFFC105.toInt() else 0xFFD96B43.toInt()
    val path = android.graphics.Path().apply {
        moveTo(size / 2f - 15f, size.toFloat() - 6f)
        lineTo(size / 2f + 15f, size.toFloat() - 6f)
        lineTo(size / 2f, size.toFloat() + 16f)
        close()
    }
    canvas.drawPath(path, paint)

    // Vibe emoji badge top-right
    val vibeEmoji = memory.foodVibe?.take(2) ?: when (memory.category) {
        "Food"     -> "🍕"
        "Cafe"     -> "☕"
        "Travel"   -> "✈️"
        "Event"    -> "🎉"
        "Landmark" -> "🏛️"
        else       -> "📍"
    }

    paint.color = 0xFFFFFDF9.toInt()
    canvas.drawCircle(size - 18f, 18f, 18f, paint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 22f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(vibeEmoji, size - 18f, 25f, textPaint)

    return bitmap
}
