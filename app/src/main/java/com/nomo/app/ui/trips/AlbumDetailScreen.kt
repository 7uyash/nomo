package com.nomo.app.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun AlbumDetailScreen(
    trip: TripEntity,
    memories: List<MemoryEntity>,
    onBackClick: () -> Unit,
    onMemoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NomoCream)
    ) {
        // Header bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NomoCream)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NomoSurface)
                    .border(1.dp, NomoCardBorder, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NomoDeepCharcoal,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Album title + count
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = trip.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal
                )
                Text(
                    text = "${memories.size} ${if (memories.size == 1) "photo" else "photos"}",
                    fontSize = 12.sp,
                    color = NomoDeepCharcoal.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        HorizontalDivider(color = NomoCardBorder.copy(alpha = 0.5f))

        if (memories.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No photos yet",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NomoDeepCharcoal
                    )
                    Text(
                        text = "Save a memory to this album from the camera",
                        style = Typography.bodyMedium,
                        color = NomoDeepCharcoal.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            // 4-column photo grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(memories, key = { it.id }) { memory ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onMemoryClick(memory.id) }
                    ) {
                        AsyncImage(
                            model = File(memory.photoPath),
                            contentDescription = memory.dishName.ifBlank { "Memory photo" },
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
