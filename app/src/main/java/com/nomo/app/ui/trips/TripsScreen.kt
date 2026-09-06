package com.nomo.app.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Luggage
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
import java.text.SimpleDateFormat
import java.util.*

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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header & Create Trip Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🧳 Trip Journeys",
                            style = Typography.headlineMedium,
                            color = NomoTerracotta
                        )
                        Text(
                            text = "Group your food & travel memories into trips",
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
                        Text("New Trip", color = NomoDeepCharcoal, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (trips.isEmpty()) {
                item {
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
                            Text(text = "🗺️ ✈️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No trips created yet",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Create a trip (like 'Jaipur Food Weekend' or 'Goa Beach Cafe Hop') to organize your map memories!",
                                style = Typography.bodyMedium,
                                color = NomoDeepCharcoal.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(trips, key = { it.id }) { trip ->
                    val tripMemories = memories.filter { it.tripId == trip.id }
                    TripItemCard(
                        trip = trip,
                        memoriesCount = tripMemories.size,
                        coverPhotoPath = trip.coverPhotoPath ?: tripMemories.firstOrNull()?.photoPath,
                        onClick = { onTripClick(trip.id) }
                    )
                }
            }
        }

        // Dialog for creating a new trip
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create New Trip Journey", fontWeight = FontWeight.Bold, color = NomoTerracotta) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTripName,
                            onValueChange = { newTripName = it },
                            label = { Text("Trip Name (e.g. Jaipur Weekend)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = newTripDesc,
                            onValueChange = { newTripDesc = it },
                            label = { Text("Description (e.g. Delhi → Jaipur food walk)") },
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
                        Text("Create Trip")
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
private fun TripItemCard(
    trip: TripEntity,
    memoriesCount: Int,
    coverPhotoPath: String?,
    onClick: () -> Unit
) {
    val dateFormatted = remember(trip.startDate) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(trip.startDate))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, NomoTerracotta.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            coverPhotoPath?.let { path ->
                AsyncImage(
                    model = File(path),
                    contentDescription = trip.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NomoCream),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(14.dp))
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NomoWarmAmber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Luggage, contentDescription = null, tint = NomoDeepCharcoal, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.name,
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal
                )
                
                trip.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Text(
                        text = desc,
                        style = Typography.bodyMedium,
                        color = NomoDeepCharcoal.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📅 $dateFormatted",
                        fontSize = 12.sp,
                        color = NomoSage,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "📌 $memoriesCount memories",
                        fontSize = 12.sp,
                        color = NomoTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Trip",
                tint = NomoTerracotta,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
