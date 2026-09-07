package com.nomo.app.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.data.local.matchesSearch
import com.nomo.app.ui.components.NomoTopBar
import com.nomo.app.ui.components.OnThisDayBanner
import com.nomo.app.ui.components.ResurfacingBanner
import com.nomo.app.ui.components.SyncStatusBadge
import com.nomo.app.ui.theme.*
import java.io.File
import java.util.Calendar

// Time filter options matching the mockup
private val TIME_FILTERS = listOf("All", "Day", "Week", "Month", "Year")

@Composable
fun ExploreScreen(
    memories: List<MemoryEntity>,
    pendingSyncCount: Int,
    resurfacingMatch: Pair<MemoryEntity, Double>?,
    onThisDayMemory: MemoryEntity?,
    onMemoryClick: (String) -> Unit,
    onProfileSyncClick: () -> Unit,
    userLat: Double = 0.0,
    userLon: Double = 0.0,
    modifier: Modifier = Modifier
) {
    var selectedTimeFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchField by remember { mutableStateOf(false) }
    var selectedMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var showResurfacing by remember { mutableStateOf(true) }
    var showOnThisDay by remember { mutableStateOf(true) }
    var centerOnUser by remember { mutableStateOf(false) }

    // Filter memories by time range
    val filteredMemories = remember(memories, selectedTimeFilter, searchQuery) {
        val now = System.currentTimeMillis()
        val cutoff = when (selectedTimeFilter) {
            "Day"   -> now - 24L * 60 * 60 * 1000
            "Week"  -> now - 7L * 24 * 60 * 60 * 1000
            "Month" -> now - 30L * 24 * 60 * 60 * 1000
            "Year"  -> now - 365L * 24 * 60 * 60 * 1000
            else    -> 0L
        }
        memories.filter { memory ->
            val matchesTime = memory.timestamp >= cutoff
            val matchesSearch = memory.matchesSearch(searchQuery)
            matchesTime && matchesSearch
        }
    }

    Box(modifier = modifier.fillMaxSize().background(NomoCream)) {

        // ── 1. Full-bleed Map ──────────────────────────────────────────────
        NomoMapView(
            memories = filteredMemories,
            selectedMemoryId = selectedMemory?.id,
            onMemorySelect = { memory -> selectedMemory = memory },
            userLat = userLat,
            userLon = userLon,
            centerOnUser = centerOnUser,
            onCenterConsumed = { centerOnUser = false }
        )

        // ── 2. Top overlay (TopBar + time filters + banner) ───────────────
        Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {

            // TopBar with logo, subtitle, search + profile
            NomoTopBar(
                pendingSyncCount = pendingSyncCount,
                onSearchClick = { showSearchField = !showSearchField },
                onProfileSyncClick = onProfileSyncClick
            )

            // Inline search field (slides down when active)
            if (showSearchField) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search places, dishes, notes…", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, NomoWarmAmber, RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = NomoSurface,
                            unfocusedContainerColor = NomoSurface,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        trailingIcon = {
                            IconButton(onClick = { searchQuery = ""; showSearchField = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = NomoDeepCharcoal)
                            }
                        }
                    )
                }
            }

            // ── Time filter pill tabs ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TIME_FILTERS.forEach { filter ->
                    val isSelected = selectedTimeFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) NomoWarmAmber else NomoSurface)
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = if (isSelected) NomoWarmAmber else NomoCardBorder,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { selectedTimeFilter = filter }
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) NomoDeepCharcoal else NomoDeepCharcoal.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // ── Resurfacing / On This Day banner ──────────────────────────
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
            } else if (showResurfacing && resurfacingMatch != null && selectedMemory == null) {
                ResurfacingBanner(
                    memory = resurfacingMatch.first,
                    distanceMeters = resurfacingMatch.second,
                    onMemoryClick = { onMemoryClick(it) },
                    onDismiss = { showResurfacing = false }
                )
            }
        }

        // ── 3. Floating map controls (right side) ─────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location / compass button
            FloatingMapButton(
                onClick = { centerOnUser = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location",
                        tint = NomoDeepCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            // Layers toggle button
            FloatingMapButton(
                onClick = { /* toggle map layer */ },
                content = {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Map Layers",
                        tint = NomoDeepCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // ── 4. Selected Memory Preview Card (bottom overlay) ──────────────
        selectedMemory?.let { memory ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .border(2.dp, NomoWarmAmber, RoundedCornerShape(24.dp))
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
                            .size(72.dp)
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
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { selectedMemory = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = NomoDeepCharcoal)
                            }
                        }

                        if (!memory.dishName.isNullOrEmpty()) {
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

/**
 * Small floating circular button for map controls (location, layers).
 */
@Composable
private fun FloatingMapButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(NomoSurface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
