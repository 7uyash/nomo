package com.nomo.app.ui.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nomo.app.data.local.MemoryEntity
import java.io.File

@Composable
fun DynamicMemoryGrid(
    memories: List<MemoryEntity>,
    onMemoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (memories.isEmpty()) return

    when (memories.size) {
        1 -> {
            // Single photo: Full width
            MemoryImage(
                memory = memories[0],
                onClick = onMemoryClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
        2 -> {
            // Two photos: Side by side
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemoryImage(
                    memory = memories[0],
                    onClick = onMemoryClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(250.dp)
                )
                MemoryImage(
                    memory = memories[1],
                    onClick = onMemoryClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(250.dp)
                )
            }
        }
        3 -> {
            // Three photos: One large on top, two smaller below
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemoryImage(
                    memory = memories[0],
                    onClick = onMemoryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MemoryImage(
                        memory = memories[1],
                        onClick = onMemoryClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    )
                    MemoryImage(
                        memory = memories[2],
                        onClick = onMemoryClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    )
                }
            }
        }
        else -> {
            // 4+ photos: standard 2-column grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.heightIn(max = 600.dp), // Constrain height in bottom sheet
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(memories) { memory ->
                    MemoryImage(
                        memory = memory,
                        onClick = onMemoryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // Square grid
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryImage(
    memory: MemoryEntity,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = File(memory.photoPath),
        contentDescription = memory.placeName,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(memory.id) },
        contentScale = ContentScale.Crop
    )
}
