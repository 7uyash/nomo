package com.nomo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResurfacingBanner(
    memory: MemoryEntity,
    distanceMeters: Double,
    onMemoryClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(memory.timestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, NomoWarmAmber, RoundedCornerShape(20.dp))
            .clickable { onMemoryClick(memory.id) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👀", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "You've been here before!",
                        style = Typography.titleMedium,
                        color = NomoTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = NomoDeepCharcoal.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = File(memory.photoPath),
                    contentDescription = memory.placeName,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NomoCream),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = memory.dishName ?: memory.placeName,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (!memory.dishName.isNull_or_empty()) {
                        Text(
                            text = "📍 ${memory.placeName}",
                            style = Typography.bodyMedium,
                            color = NomoDeepCharcoal.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        memory.foodVibe?.let { vibe ->
                            Text(text = vibe, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "Visited $dateStr (${distanceMeters.toInt()}m away)",
                            fontSize = 11.sp,
                            color = NomoSage,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * "On This Day" Memory Resurfacing Banner ("A year ago today 📍 You were here.")
 */
@Composable
fun OnThisDayBanner(
    memory: MemoryEntity,
    yearsAgo: Int,
    onMemoryClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(memory.timestamp))
    val timeLabel = if (yearsAgo == 1) "A year ago today" else "$yearsAgo years ago today"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, NomoTerracotta, RoundedCornerShape(20.dp))
            .clickable { onMemoryClick(memory.id) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅 📍", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$timeLabel... You were here!",
                        style = Typography.titleMedium,
                        color = NomoTerracotta,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = NomoDeepCharcoal.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = File(memory.photoPath),
                    contentDescription = memory.placeName,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NomoCream),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = memory.dishName ?: memory.placeName,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (!memory.dishName.isNull_or_empty()) {
                        Text(
                            text = "📍 ${memory.placeName}",
                            style = Typography.bodyMedium,
                            color = NomoDeepCharcoal.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        memory.foodVibe?.let { vibe ->
                            Text(text = vibe, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = NomoSage,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
