package com.nomo.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
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
import com.nomo.app.ui.components.FoodCategoryChip
import com.nomo.app.ui.components.SyncStatusBadge
import com.nomo.app.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemoryDetailScreen(
    memory: MemoryEntity,
    nearbyMemories: List<MemoryEntity>,
    onBackClick: () -> Unit,
    onDeleteClick: (MemoryEntity) -> Unit,
    onMemoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatted = remember(memory.timestamp) {
        SimpleDateFormat("MMMM d, yyyy · h:mm a", Locale.getDefault()).format(Date(memory.timestamp))
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NomoCream)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NomoDeepCharcoal)
                }

                Text(
                    text = memory.category,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NomoSage
                )

                IconButton(onClick = { onDeleteClick(memory) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Memory", tint = SyncFailedRed)
                }
            }
        },
        containerColor = NomoCream,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Emotional Centerpiece: Large Photo Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.5.dp, NomoSage, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    AsyncImage(
                        model = File(memory.photoPath),
                        contentDescription = memory.placeName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Food Vibe Tag Badge Overlay
                    memory.foodVibe?.let { vibe ->
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomStart)
                                .background(NomoSurface, RoundedCornerShape(16.dp))
                                .border(1.dp, NomoWarmAmber, RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = vibe,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NomoDeepCharcoal
                            )
                        }
                    }

                    // Sync Status Badge Overlay
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        SyncStatusBadge(status = memory.syncStatus)
                    }
                }
            }

            // 2. Story Header & Details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NomoSurface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = memory.dishName ?: memory.placeName,
                        style = Typography.headlineMedium,
                        color = NomoDeepCharcoal,
                        fontWeight = FontWeight.Black
                    )

                    if (!memory.dishName.isNull_or_empty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = NomoSage, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = memory.placeName,
                                style = Typography.titleMedium,
                                color = NomoSage,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dateFormatted,
                        style = Typography.bodyMedium,
                        color = NomoDeepCharcoal.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )

                    memory.note?.takeIf { it.isNotBlank() }?.let { noteText ->
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NomoCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "“$noteText”",
                            style = Typography.bodyLarge,
                            color = NomoDeepCharcoal,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // 3. Related Nearby Memories Carousel
            if (nearbyMemories.isNotEmpty()) {
                Column {
                    Text(
                        text = "Related Nearby Memories 📍",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NomoSage
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        nearbyMemories.forEach { nearby ->
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .border(1.5.dp, NomoCardBorder, RoundedCornerShape(16.dp))
                                    .clickable { onMemoryClick(nearby.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = NomoSurface)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = File(nearby.photoPath),
                                        contentDescription = nearby.placeName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = nearby.dishName ?: nearby.placeName,
                                            style = Typography.titleMedium,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = nearby.placeName,
                                            fontSize = 11.sp,
                                            color = NomoDeepCharcoal.copy(alpha = 0.7f),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
