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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
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
    availableTrips: List<TripEntity>,
    onSaveMemory: (
        dishName: String,
        placeName: String,
        foodVibe: String,
        category: String,
        note: String,
        selectedTripId: String?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var dishName by remember { mutableStateOf("") }
    var placeName by remember { mutableStateOf(initialPlaceName.ifBlank { "My Experience Spot" }) }
    var selectedVibe by remember { mutableStateOf("🍕 Tasty") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var note by remember { mutableStateOf("") }
    var selectedTripId by remember { mutableStateOf<String?>(null) }

    val vibes = listOf("🍕 Tasty", "☕ Cozy", "🍜 Yum", "🌶️ Spicy", "🍰 Sweet", "🍷 Chill", "⭐ Magical", "😋 Delicious")
    val categories = listOf("Food", "Cafe", "Travel", "Event", "Shopping", "Landmark", "Personal")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, NomoTerracotta, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag indicator handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NomoCardBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "New Memory Captured! 📸",
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NomoTerracotta
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    bitmap = photoBitmap.asImageBitmap(),
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, NomoWarmAmber, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Place Name Input
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Place / Location Name", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = NomoTerracotta) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "GPS: %.4f, %.4f".format(latitude, longitude),
                        fontSize = 11.sp,
                        color = NomoSage,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dish / Thing Name
            OutlinedTextField(
                value = dishName,
                onValueChange = { dishName = it },
                label = { Text("What did you eat or experience? (e.g. Bun Maska & Chai)", fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Food Vibe Selector
            Text(
                text = "Food Vibe / Feeling",
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
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) NomoTerracotta else NomoCardBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedVibe = vibe }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = vibe,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = NomoDeepCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Picker
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
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Optional Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Personal memory note...", fontSize = 12.sp) },
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
                    .height(52.dp),
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
