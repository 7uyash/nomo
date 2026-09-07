package com.nomo.app.ui.capture

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var placeName by remember { mutableStateOf(initialPlaceName.ifBlank { "My Experience Spot" }) }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedVibe by remember { mutableStateOf("⭐ Loved It") }
    var note by remember { mutableStateOf("") }
    var selectedTripId by remember { mutableStateOf<String?>(null) }

    var isEditing by remember { mutableStateOf(false) }

    val categories = listOf("Food", "Cafe", "Travel", "Event", "Shopping", "Personal")
    
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF9F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Indicator
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NomoCardBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Info Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Thumbnail
                Image(
                    bitmap = photoBitmap.asImageBitmap(),
                    contentDescription = "Captured memory photo",
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, NomoCardBorder, RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Metadata Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "READY TO SAVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (dishName.isNotBlank()) dishName else placeName,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomoDeepCharcoal,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$selectedCategory · Today · Private",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NomoDeepCharcoal.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Edit Button (Pencil Icon)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isEditing) NomoWarmAmber else Color(0xFFE8F2EC))
                        .clickable { isEditing = !isEditing },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit details",
                        tint = NomoDeepCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded Edit Options (Toggled via Pencil Button)
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = NomoCardBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Title Input
                    OutlinedTextField(
                        value = dishName,
                        onValueChange = { dishName = it },
                        label = { Text("Title / Memory Name", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Majnu chai stop, Cold Coffee", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location Spot Input
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Location / Place Name", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = NomoTerracotta, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selector
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomoDeepCharcoal
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

                    // Vibe Selector
                    Text(
                        text = "Mood / Vibe",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NomoDeepCharcoal
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

                    // Trip Tagging (if available)
                    if (availableTrips.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Add to Trip",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomoDeepCharcoal
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
                                        .clickable { selectedTripId = if (isSelected) null else trip.id }
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

                    // Personal Note Field
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Add Note / Story", fontSize = 12.sp) },
                        placeholder = { Text("Write personal thoughts or tips...", fontSize = 11.sp) },
                        maxLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons Row: Later & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Later Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(0.4f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NomoCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = NomoDeepCharcoal
                    )
                ) {
                    Text(
                        text = "Later",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Save Button
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
                        .weight(0.6f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF4B41A),
                        contentColor = NomoDeepCharcoal
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = NomoDeepCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Note
            Text(
                text = "Everything can be edited later",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = NomoDeepCharcoal.copy(alpha = 0.55f)
            )
        }
    }
}
