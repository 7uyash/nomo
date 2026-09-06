package com.nomo.app.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun TimelineScreen(
    memories: List<MemoryEntity>,
    onMemoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group memories by Month Year string
    val groupedMemories = remember(memories) {
        memories.groupBy { memory ->
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(memory.timestamp))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NomoCream)
    ) {
        if (memories.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "📸 🍕 🗺️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your memory timeline is empty",
                    style = Typography.titleLarge,
                    color = NomoDeepCharcoal
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the camera button to capture your first place or food experience!",
                    style = Typography.bodyLarge,
                    color = NomoDeepCharcoal.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Timeline Header Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, NomoTerracotta, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NomoSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📖 My Life Diary",
                                style = Typography.headlineMedium,
                                color = NomoTerracotta
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${memories.size} memories collected across places & dishes",
                                style = Typography.bodyLarge,
                                color = NomoDeepCharcoal.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                groupedMemories.forEach { (monthHeader, monthMemories) ->
                    item {
                        Text(
                            text = monthHeader,
                            style = Typography.titleLarge,
                            color = NomoSage,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(monthMemories, key = { it.id }) { memory ->
                        TimelineMemoryItem(
                            memory = memory,
                            onClick = { onMemoryClick(memory.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineMemoryItem(
    memory: MemoryEntity,
    onClick: () -> Unit
) {
    val dateFormatted = remember(memory.timestamp) {
        SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(memory.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = File(memory.photoPath),
                    contentDescription = memory.placeName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                // Category overlay chip
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    FoodCategoryChip(category = memory.category, isSelected = true)
                }

                // Sync status badge overlay
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                ) {
                    SyncStatusBadge(status = memory.syncStatus)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = memory.dishName ?: memory.placeName,
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NomoDeepCharcoal,
                        modifier = Modifier.weight(1f)
                    )

                    memory.foodVibe?.let { vibe ->
                        Text(text = vibe, fontSize = 16.sp)
                    }
                }

                if (!memory.dishName.isNull_or_empty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📍 ${memory.placeName}",
                        style = Typography.titleMedium,
                        color = NomoTerracotta,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = dateFormatted,
                    fontSize = 12.sp,
                    color = NomoDeepCharcoal.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                memory.note?.takeIf { it.isNotBlank() }?.let { noteText ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“$noteText”",
                        style = Typography.bodyMedium,
                        color = NomoDeepCharcoal.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
