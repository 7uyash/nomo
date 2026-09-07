package com.nomo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomo.app.R
import com.nomo.app.ui.theme.*

@Composable
fun NomoTopBar(
    subtitle: String = "Places. People. Food. Memories.",
    pendingSyncCount: Int = 0,
    onSearchClick: () -> Unit = {},
    onProfileSyncClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NomoCream)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Logo image + subtitle
        Column {
            // NOMO wordmark via drawable
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.nomo_logo),
                contentDescription = "NOMO",
                modifier = Modifier.height(36.dp)
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = NomoDeepCharcoal.copy(alpha = 0.55f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }

        // Right: Search icon + Profile avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search icon button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NomoSurface)
                    .border(1.dp, NomoCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NomoDeepCharcoal,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Profile / Settings avatar circle
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NomoDeepCharcoal)
                    .clickable { onProfileSyncClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 18.sp)
            }
        }
    }
}
