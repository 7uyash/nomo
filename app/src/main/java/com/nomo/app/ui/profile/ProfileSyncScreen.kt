package com.nomo.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.ui.theme.*

@Composable
fun ProfileSyncScreen(
    googleAccountName: String? = null,
    memories: List<MemoryEntity>,
    onConnectGoogleClick: () -> Unit = {},
    onSyncNowClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val photoCount = memories.count { it.photoPath.isNotBlank() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NomoCream)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NomoTerracotta, RoundedCornerShape(26.dp)),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NomoWarmAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⚙️", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Settings & Storage",
                                    style = Typography.headlineMedium,
                                    color = NomoTerracotta,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Personal Vault & Device Sync",
                                    fontSize = 13.sp,
                                    color = NomoDeepCharcoal.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Local Gallery Album & Storage Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Local Gallery & Album Status",
                            style = Typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NomoDeepCharcoal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Album Pill Status Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = NomoTerracotta,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gallery Album: NOMO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NomoDeepCharcoal
                                    )
                                    Text(
                                        text = "Pictures/NOMO folder",
                                        fontSize = 12.sp,
                                        color = NomoDeepCharcoal.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                    .border(1.dp, NomoSage, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NomoSage,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Active",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomoSage
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Privacy Philosophy Guarantee Box
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NomoSage, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privacy Guarantee",
                            tint = NomoSage,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "100% On-Device Storage",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NomoSage
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your photos, locations, and personal notes are saved exclusively on your phone. No central company database ever accesses your private life map.",
                                style = Typography.bodyMedium,
                                color = NomoDeepCharcoal.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // App Information & About Footer Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = NomoDeepCharcoal.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "About NOMO",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NomoDeepCharcoal
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "App Version", fontSize = 13.sp, color = NomoDeepCharcoal.copy(alpha = 0.7f))
                            Text(text = "v1.0.0 (Local Vault)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NomoDeepCharcoal)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Storage Engine", fontSize = 13.sp, color = NomoDeepCharcoal.copy(alpha = 0.7f))
                            Text(text = "Android Room + MediaStore", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NomoTerracotta)
                        }
                    }
                }
            }
        }
    }
}
