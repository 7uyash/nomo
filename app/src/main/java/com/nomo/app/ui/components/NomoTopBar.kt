package com.nomo.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
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
import com.nomo.app.ui.theme.*

@Composable
fun NomoTopBar(
    title: String = "NOMO",
    subtitle: String = "Remembers where you've been",
    pendingSyncCount: Int = 0,
    onProfileSyncClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NomoCream)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NomoTerracotta)
                    .border(2.dp, NomoWarmAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗺️", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = Typography.titleLarge,
                    color = NomoDeepCharcoal,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = NomoDeepCharcoal.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        IconButton(
            onClick = onProfileSyncClick,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(NomoSurface)
                .border(1.dp, NomoCardBorder, RoundedCornerShape(12.dp))
        ) {
            BadgedBox(
                badge = {
                    if (pendingSyncCount > 0) {
                        Badge(containerColor = NomoWarmAmber) {
                            Text(text = "$pendingSyncCount", color = NomoDeepCharcoal, fontSize = 10.sp)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Sync",
                    tint = NomoTerracotta
                )
            }
        }
    }
}
