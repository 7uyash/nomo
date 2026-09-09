package com.nomo.app.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.data.local.TripEntity
import com.nomo.app.ui.theme.*
import java.io.File

@Composable
fun TripsScreen(
    trips: List<TripEntity>,
    memories: List<MemoryEntity>,
    onTripClick: (String) -> Unit,
    onCreateTripClick: (name: String, desc: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTripName by remember { mutableStateOf("") }
    var newTripDesc by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NomoCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header & Create Collection Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Food Collections",
                        style = Typography.headlineMedium,
                        color = NomoSage,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Organize your food experiences into albums",
                        style = Typography.bodyMedium,
                        color = NomoDeepCharcoal.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomoWarmAmber, contentColor = NomoDeepCharcoal),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NomoDeepCharcoal, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Collection", color = NomoDeepCharcoal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (trips.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No collections created yet",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NomoDeepCharcoal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'New Collection' to create custom albums like Momos, Noodles, or Best Places.",
                            style = Typography.bodyMedium,
                            color = NomoDeepCharcoal.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(trips, key = { it.id }) { trip ->
                        val tripMemories = memories.filter { it.tripId == trip.id }
                        AlbumFolderCard(
                            trip = trip,
                            memoriesCount = tripMemories.size,
                            coverPhotoPath = trip.coverPhotoPath ?: tripMemories.firstOrNull()?.photoPath,
                            onClick = { onTripClick(trip.id) }
                        )
                    }
                }
            }
        }

        // Dialog for creating a new collection
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create New Food Collection", fontWeight = FontWeight.Bold, color = NomoSage) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTripName,
                            onValueChange = { newTripName = it },
                            label = { Text("Collection Name (e.g. Momos, Noodles, Best Places)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NomoDeepCharcoal,
                                unfocusedTextColor = NomoDeepCharcoal,
                                focusedLabelColor = NomoDeepCharcoal,
                                unfocusedLabelColor = NomoDeepCharcoal.copy(alpha = 0.7f),
                                focusedPlaceholderColor = NomoDeepCharcoal.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = NomoDeepCharcoal.copy(alpha = 0.5f),
                                focusedBorderColor = NomoSage,
                                cursorColor = NomoDeepCharcoal
                            )
                        )
                        OutlinedTextField(
                            value = newTripDesc,
                            onValueChange = { newTripDesc = it },
                            label = { Text("Description (Optional)") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NomoDeepCharcoal,
                                unfocusedTextColor = NomoDeepCharcoal,
                                focusedLabelColor = NomoDeepCharcoal,
                                unfocusedLabelColor = NomoDeepCharcoal.copy(alpha = 0.7f),
                                focusedPlaceholderColor = NomoDeepCharcoal.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = NomoDeepCharcoal.copy(alpha = 0.5f),
                                focusedBorderColor = NomoSage,
                                cursorColor = NomoDeepCharcoal
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTripName.isNotBlank()) {
                                onCreateTripClick(newTripName, newTripDesc)
                                newTripName = ""
                                newTripDesc = ""
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NomoWarmAmber, contentColor = NomoDeepCharcoal)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = NomoSurface,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
private fun AlbumFolderCard(
    trip: TripEntity,
    memoriesCount: Int,
    coverPhotoPath: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NomoCream),
                contentAlignment = Alignment.Center
            ) {
                if (!coverPhotoPath.isNullOrBlank()) {
                    AsyncImage(
                        model = File(coverPhotoPath),
                        contentDescription = trip.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = NomoSage,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trip.name,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NomoDeepCharcoal,
                maxLines = 1
            )

            Text(
                text = "$memoriesCount photos",
                fontSize = 11.sp,
                color = NomoSage,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
