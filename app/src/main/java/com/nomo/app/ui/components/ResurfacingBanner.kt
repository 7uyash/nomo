package com.nomo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Compact proximity-resurfacing banner.
 * Layout matches the mockup: photo thumb | text block | yellow "View →" pill button
 */
@Composable
fun ResurfacingBanner(
    memory: MemoryEntity,
    distanceMeters: Double,
    onMemoryClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(memory.timestamp))
    val categoryLabel = memory.category.replaceFirstChar { it.uppercase() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo thumbnail
            AsyncImage(
                model = File(memory.photoPath),
                contentDescription = memory.placeName,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NomoCream),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text block
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🍴", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "You've been here before ✦✦",
                        fontSize = 12.sp,
                        color = NomoDeepCharcoal.copy(alpha = 0.65f),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = memory.dishName ?: memory.placeName,
                    style = Typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal,
                    maxLines = 1
                )
                Text(
                    text = "$dateStr · $categoryLabel",
                    fontSize = 11.sp,
                    color = NomoDeepCharcoal.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Yellow "View →" pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(NomoWarmAmber)
                    .clickable { onMemoryClick(memory.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal
                )
            }
        }
    }
}

/**
 * "On This Day" Memory Resurfacing Banner — same compact style.
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
    val categoryLabel = memory.category.replaceFirstChar { it.uppercase() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo thumbnail
            AsyncImage(
                model = File(memory.photoPath),
                contentDescription = memory.placeName,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NomoCream),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$timeLabel ✦✦",
                        fontSize = 12.sp,
                        color = NomoDeepCharcoal.copy(alpha = 0.65f),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = memory.dishName ?: memory.placeName,
                    style = Typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal,
                    maxLines = 1
                )
                Text(
                    text = "$dateStr · $categoryLabel",
                    fontSize = 11.sp,
                    color = NomoDeepCharcoal.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Terracotta "View →" pill button (slight variation to distinguish)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(NomoWarmAmber)
                    .clickable { onMemoryClick(memory.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal
                )
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
