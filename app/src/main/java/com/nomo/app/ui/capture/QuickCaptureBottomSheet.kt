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
import androidx.compose.material.icons.filled.Folder
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
    val placeName by remember { mutableStateOf(initialPlaceName.ifBlank { "Captured Location" }) }
    var note by remember { mutableStateOf("") }
    var selectedTripId by remember { mutableStateOf<String?>(null) }

    var isEditing by remember { mutableStateOf(false) }

    val displayTitle = if (dishName.isNotBlank()) dishName else "Memory Title"

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
            // Drag Indicator Handle
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NomoCardBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Post-Picture Card Header Row
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

                // Title & Subtitle Column (Clickable to open edit mode)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isEditing = !isEditing }
                ) {
                    Text(
                        text = "READY TO SAVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = displayTitle,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dishName.isNotBlank()) NomoDeepCharcoal else NomoDeepCharcoal.copy(alpha = 0.45f),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$placeName · Today · Private",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NomoDeepCharcoal.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Edit Pencil Button
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
                        contentDescription = "Edit memory title and note",
                        tint = NomoDeepCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded Edit Mode (Title and Optional Note)
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
                        label = { Text("Memory Title", fontSize = 12.sp) },
                        placeholder = { Text("Enter a title for this memory...", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Memory Note Input
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Add Note / Highlight (Optional)", fontSize = 12.sp) },
                        placeholder = { Text("Write personal details, story, or recommendations...", fontSize = 11.sp) },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Add to Album / Folder Selector
            if (availableTrips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Add to Album / Folder",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal,
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
                                .clickable { selectedTripId = if (isSelected) null else trip.id }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = if (isSelected) NomoCream else NomoTerracotta,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = trip.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NomoCream else NomoDeepCharcoal
                                )
                            }
                        }
                    }
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
                            "",          // No food vibe
                            "Personal",  // Default category
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
