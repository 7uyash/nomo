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

    // Center map on user location as soon as userLat/userLon updates to valid coordinates
    LaunchedEffect(userLat, userLon) {
        if (userLat != 0.0 && userLon != 0.0) {
            mapView.controller.setCenter(GeoPoint(userLat, userLon))
            mapView.controller.setZoom(16.0)
        } else if (memories.isNotEmpty()) {
            mapView.controller.setCenter(GeoPoint(memories.first().latitude, memories.first().longitude))
        }
    }

    // Re-center on user whenever centerOnUser is triggered
    LaunchedEffect(centerOnUser) {
        if (centerOnUser && userLat != 0.0 && userLon != 0.0) {
            mapView.controller.animateTo(GeoPoint(userLat, userLon))
            onCenterConsumed()
        }
    }

    // Redraw memory markers & user position marker whenever state changes
    LaunchedEffect(memories, selectedMemoryId, userLat, userLon) {
        // Keep myLocationOverlay; clear only marker overlays
        mapView.overlays.removeIf { it is Marker }

        // 1. User Current Location Pin Marker
        if (userLat != 0.0 && userLon != 0.0) {
            val userMarker = Marker(mapView).apply {
                position = GeoPoint(userLat, userLon)
                title = "You Are Here"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                val iconBitmap = createUserPinBitmap()
                icon = BitmapDrawable(context.resources, iconBitmap)
            }
            mapView.overlays.add(userMarker)
        }

        // 2. Memory Markers
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

/**
 * Creates a yellow #FFC105 rounded square pin with a person emoji — "You Are Here" marker.
 */
private fun createUserPinBitmap(): Bitmap {
    val size = 100
    val bitmap = Bitmap.createBitmap(size, size + 20, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Yellow rounded square background
    paint.color = 0xFFFFC105.toInt()
    val bgRect = RectF(4f, 4f, size.toFloat() - 4f, size.toFloat() - 4f)
    canvas.drawRoundRect(bgRect, 24f, 24f, paint)

    // White inner circle
    paint.color = 0xFFFFFFFF.toInt()
    canvas.drawCircle(size / 2f, size / 2f, size / 3f, paint)

    // Pin pointer triangle at bottom
    paint.color = 0xFFFFC105.toInt()
    val path = android.graphics.Path().apply {
        moveTo(size / 2f - 14f, size.toFloat() - 5f)
        lineTo(size / 2f + 14f, size.toFloat() - 5f)
        lineTo(size / 2f, size.toFloat() + 15f)
        close()
    }
    canvas.drawPath(path, paint)

    // Person emoji in the center
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 36f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("🧍", size / 2f, size / 2f + 14f, textPaint)

    return bitmap
}
