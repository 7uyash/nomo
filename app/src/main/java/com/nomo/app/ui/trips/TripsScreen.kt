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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
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
            // Header & Create Album Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📁 Albums & Folders",
                        style = Typography.headlineMedium,
                        color = NomoTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Organize photos into Momos, Noodles, Best Places...",
                        style = Typography.bodyMedium,
                        color = NomoDeepCharcoal.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NomoWarmAmber)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NomoDeepCharcoal)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Album", color = NomoDeepCharcoal, fontWeight = FontWeight.Bold)
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
                        Text(text = "📁 🥟 🍜", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No custom albums yet",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'New Album' to create folders like Momos, Noodles, Best Places, or Food Finds!",
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

        // Dialog for creating a new album folder
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create New Album Folder", fontWeight = FontWeight.Bold, color = NomoTerracotta) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTripName,
                            onValueChange = { newTripName = it },
                            label = { Text("Album Name (e.g. 🥟 Momos, 🍜 Noodles)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = newTripDesc,
                            onValueChange = { newTripDesc = it },
                            label = { Text("Folder Description (Optional)") },
                            shape = RoundedCornerShape(12.dp)
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
                        colors = ButtonDefaults.buttonColors(containerColor = NomoTerracotta)
                    ) {
                        Text("Create Folder")
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
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = NomoTerracotta,
                        modifier = Modifier.size(44.dp)
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
