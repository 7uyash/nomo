package com.nomo.app.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.nomo.app.ui.components.NomoTopBar
import com.nomo.app.ui.components.OnThisDayBanner
import com.nomo.app.ui.components.ResurfacingBanner
import com.nomo.app.ui.components.SyncStatusBadge
import com.nomo.app.ui.theme.*
import java.io.File
import java.util.Calendar

@Composable
fun ExploreScreen(
    memories: List<MemoryEntity>,
    pendingSyncCount: Int,
    resurfacingMatch: Pair<MemoryEntity, Double>?,
    onThisDayMemory: MemoryEntity?,
    onMemoryClick: (String) -> Unit,
    onProfileSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchField by remember { mutableStateOf(false) }
    var selectedMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var showResurfacing by remember { mutableStateOf(true) }
    var showOnThisDay by remember { mutableStateOf(true) }

    val categories = listOf("All", "Food", "Cafe", "Travel", "Event", "Shopping", "Landmark", "Personal")

    val filteredMemories = remember(memories, selectedCategory, searchQuery) {
        memories.filter { memory ->
            val matchesCategory = selectedCategory == "All" || memory.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    memory.placeName.contains(searchQuery, ignoreCase = true) ||
                    (memory.dishName?.contains(searchQuery, ignoreCase = true) == true) ||
                    (memory.note?.contains(searchQuery, ignoreCase = true) == true)
            matchesCategory && matchesSearch
        }
    }

    Box(modifier = modifier.fillMaxSize().background(NomoCream)) {
        // 1. Maplibre / OSM Interactive Personal Map
        NomoMapView(
            memories = filteredMemories,
            selectedMemoryId = selectedMemory?.id,
            onMemorySelect = { memory -> selectedMemory = memory }
        )

        // 2. Top Header & Category Filter Bar
        Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
            NomoTopBar(
                pendingSyncCount = pendingSyncCount,
                onProfileSyncClick = onProfileSyncClick
            )

            // Search Bar & Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showSearchField) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search places, dishes, notes...", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, NomoTerracotta, RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NomoSurface,
                            unfocusedContainerColor = NomoSurface,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        trailingIcon = {
                            IconButton(onClick = { searchQuery = ""; showSearchField = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close search", tint = NomoDeepCharcoal)
                            }
                        }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .weight(1f)
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

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showSearchField = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NomoSurface)
                            .border(1.dp, NomoCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = NomoDeepCharcoal)
                    }
                }
            }

            // On This Day Banner ("A year ago today 📍 You were here.")
            if (showOnThisDay && onThisDayMemory != null && selectedMemory == null) {
                val nowYear = Calendar.getInstance().get(Calendar.YEAR)
                val memYear = Calendar.getInstance().apply { timeInMillis = onThisDayMemory.timestamp }.get(Calendar.YEAR)
                val yearsAgo = (nowYear - memYear).coerceAtLeast(1)

                OnThisDayBanner(
                    memory = onThisDayMemory,
                    yearsAgo = yearsAgo,
                    onMemoryClick = { onMemoryClick(it) },
                    onDismiss = { showOnThisDay = false }
                )
            }
            // Location Proximity Resurfacing Banner ("You've been here before 👀")
            else if (showResurfacing && resurfacingMatch != null && selectedMemory == null) {
                ResurfacingBanner(
                    memory = resurfacingMatch.first,
                    distanceMeters = resurfacingMatch.second,
                    onMemoryClick = { onMemoryClick(it) },
                    onDismiss = { showResurfacing = false }
                )
            }
        }

        // 3. Selected Memory Preview Card (Bottom Overlay)
        selectedMemory?.let { memory ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .border(2.dp, NomoTerracotta, RoundedCornerShape(24.dp))
                    .clickable { onMemoryClick(memory.id) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NomoSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = File(memory.photoPath),
                        contentDescription = memory.placeName,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NomoCream),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = memory.dishName ?: memory.placeName,
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            IconButton(
                                onClick = { selectedMemory = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = NomoDeepCharcoal)
                            }
                        }

                        if (!memory.dishName.isNull_or_empty()) {
                            Text(
                                text = "📍 ${memory.placeName}",
                                style = Typography.bodyMedium,
                                color = NomoDeepCharcoal.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            memory.foodVibe?.let { vibe ->
                                Text(text = vibe, fontSize = 13.sp)
                            }
                            SyncStatusBadge(status = memory.syncStatus)
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Memory",
                        tint = NomoTerracotta,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
