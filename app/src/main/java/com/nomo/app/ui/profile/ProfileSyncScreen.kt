package com.nomo.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            item {
                // Header Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NomoTerracotta, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "⚙️ Settings & Storage",
                            style = Typography.headlineMedium,
                            color = NomoTerracotta
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your memories belong to you. Saved directly on your device.",
                            style = Typography.bodyLarge,
                            color = NomoDeepCharcoal.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Local Gallery Album Info Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = NomoTerracotta,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Device Gallery Album",
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Album Name: NOMO",
                                    style = Typography.bodyMedium,
                                    color = NomoDeepCharcoal.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NomoCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = NomoSage,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pictures/NOMO on device storage",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NomoSage
                            )
                        }
                    }
                }
            }

            // Storage Stats
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Storage Summary",
                            style = Typography.titleLarge,
                            color = NomoDeepCharcoal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(title = "Total Memories", count = memories.size, color = NomoTerracotta)
                            StatBox(title = "Photos Saved", count = memories.count { it.photoPath.isNotBlank() }, color = NomoSage)
                        }
                    }
                }
            }

            // Privacy Philosophy Guarantee Box
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, NomoSage, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Privacy Guarantee",
                            tint = NomoSage,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Privacy-First Architecture",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NomoSage
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "NOMO stores your photos and location history exclusively on your local device. No central company database ever sees your personal life map.",
                                style = Typography.bodyMedium,
                                color = NomoDeepCharcoal.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(title: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(NomoCream, RoundedCornerShape(14.dp))
            .border(1.dp, NomoCardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(text = "$count", fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NomoDeepCharcoal)
    }
}
