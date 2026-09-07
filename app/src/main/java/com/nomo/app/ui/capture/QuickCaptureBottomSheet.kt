package com.nomo.app.ui.capture

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomo.app.data.local.TripEntity
import com.nomo.app.ui.components.FoodCategoryChip
import com.nomo.app.ui.theme.*

@Composable
fun QuickCaptureBottomSheet(
    photoBitmap: Bitmap,
    initialPlaceName: String = "",
    latitude: Double,
    longitude: Double,
    availableTrips: List<TripEntity> = emptyList(),
    onSaveMemory: (
        dishName: String,
        placeName: String,
        foodVibe: String,
        category: String,
        note: String,
        selectedTripId: String?
    ) -> Unit,
    onDismiss: () -> Unit = {}
) {
    var dishName by remember { mutableStateOf("") }
    var placeName by remember { mutableStateOf(initialPlaceName.ifBlank { "My Experienced Place" }) }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedVibe by remember { mutableStateOf("⭐ Loved It") }
    var note by remember { mutableStateOf("") }
    var selectedTripId by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Food", "Cafe", "Travel", "Event", "Shopping", "Personal")
    
    // Dynamic vibe options based on category choice
    val vibes = when (selectedCategory) {
        "Food" -> listOf("🍕 Super Tasty", "😋 Yum", "🌶️ Spicy", "🍰 Sweet", "🍷 Chill", "⭐ Loved It")
        "Cafe" -> listOf("☕ Cozy & Chill", "🍰 Sweet Treat", "📖 Quiet Corner", "⭐ Loved It")
        "Travel" -> listOf("📸 Scenic View", "🌅 Sunset Spot", "🏞️ Epic Place", "⭐ Loved It")
        "Event" -> listOf("🎉 Fun Vibe", "🎵 Great Music", "✨ Magical", "⭐ Loved It")
        else -> listOf("⭐ Loved It", "☕ Cozy", "📸 Scenic", "🎉 Fun Vibe", "🧘 Peaceful")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(2.dp, NomoTerracotta, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NomoCardBorder)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "New Memory Captured! 📸",
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NomoTerracotta
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Photo Preview + Location Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = photoBitmap.asImageBitmap(),
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, NomoWarmAmber, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Location / Spot Name", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = NomoTerracotta, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .background(NomoCream, RoundedCornerShape(8.dp))
                            .border(1.dp, NomoCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "📍 GPS: %.4f, %.4f".format(latitude, longitude),
                            fontSize = 10.sp,
                            color = NomoSage,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Memory Title Input
            OutlinedTextField(
                value = dishName,
                onValueChange = { dishName = it },
                label = { Text("Memory Title / What is this?", fontSize = 12.sp) },
                placeholder = { Text("e.g. Cold Brew & Brownie, Taj Sunset", fontSize = 11.sp, color = NomoDeepCharcoal.copy(alpha = 0.4f)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selector
            Text(
                text = "Category",
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FoodCategoryChip(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            // Reset vibe to default first available vibe
                            selectedVibe = when (cat) {
                                "Food" -> "🍕 Super Tasty"
                                "Cafe" -> "☕ Cozy & Chill"
                                "Travel" -> "📸 Scenic View"
                                "Event" -> "🎉 Fun Vibe"
                                else -> "⭐ Loved It"
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mood / Vibe Selector
            Text(
                text = "Mood / Vibe",
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vibes.forEach { vibe ->
                    val isSelected = selectedVibe == vibe
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) NomoWarmAmber else NomoCream,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) NomoTerracotta else NomoCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedVibe = vibe }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = vibe,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = NomoDeepCharcoal
                        )
                    }
                }
            }

            // Trip / Collection Tagger (if available)
            if (availableTrips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Add to Trip / Collection",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTrips.forEach { trip ->
                        val isSelected = selectedTripId == trip.id
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NomoSage else NomoCream,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (isSelected) NomoTerracotta else NomoCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedTripId = if (isSelected) null else trip.id
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Luggage,
                                    contentDescription = null,
                                    tint = if (isSelected) NomoCream else NomoDeepCharcoal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = trip.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NomoCream else NomoDeepCharcoal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Optional Memory Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Memory Highlight (Optional)", fontSize = 12.sp) },
                placeholder = { Text("Write any personal details or recommendations...", fontSize = 11.sp, color = NomoDeepCharcoal.copy(alpha = 0.4f)) },
                maxLines = 2,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Memory Button
            Button(
                onClick = {
                    onSaveMemory(
                        dishName,
                        placeName,
                        selectedVibe,
                        selectedCategory,
                        note,
                        selectedTripId
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NomoTerracotta)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = NomoCream)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Memory to My Map",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomoCream
                )
            }
        }
    }
}
