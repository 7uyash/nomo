package com.nomo.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.data.sync.SyncStatus
import com.nomo.app.ui.components.SyncStatusBadge
import com.nomo.app.ui.theme.*

@Composable
fun ProfileSyncScreen(
    googleAccountName: String?,
    memories: List<MemoryEntity>,
    onConnectGoogleClick: () -> Unit,
    onSyncNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val syncedCount = memories.count { it.syncStatus == SyncStatus.SYNCED }
    val pendingCount = memories.count { it.syncStatus != SyncStatus.SYNCED }

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
                            text = "⚙️ Sync & Cloud Storage",
                            style = Typography.headlineMedium,
                            color = NomoTerracotta
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your memories belong to you. Zero central database.",
                            style = Typography.bodyLarge,
                            color = NomoDeepCharcoal.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Google Drive Connection Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = NomoSage,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Google Drive (`drive.file`)",
                                        style = Typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = googleAccountName ?: "Not connected",
                                        style = Typography.bodyMedium,
                                        color = NomoDeepCharcoal.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Button(
                                onClick = onConnectGoogleClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (googleAccountName != null) NomoCream else NomoTerracotta
                                )
                            ) {
                                Text(
                                    text = if (googleAccountName != null) "Connected ✅" else "Connect Drive",
                                    color = if (googleAccountName != null) NomoSage else NomoCream,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = NomoCardBorder)

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Drive Storage Location:",
                            style = Typography.titleMedium,
                            fontSize = 13.sp,
                            color = NomoDeepCharcoal.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Google Drive / NOMO_Memories / (Photos & Memories)",
                            style = Typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NomoTerracotta
                        )
                    }
                }
            }

            // Sync Queue Stats
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NomoCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NomoSurface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sync Queue Status",
                                style = Typography.titleLarge,
                                color = NomoDeepCharcoal
                            )

                            IconButton(
                                onClick = onSyncNowClick,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NomoWarmAmber)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Sync Now", tint = NomoDeepCharcoal)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(title = "Total Memories", count = memories.size, color = NomoTerracotta)
                            StatBox(title = "Synced ☁️", count = syncedCount, color = NomoSage)
                            StatBox(title = "Pending ⏳", count = pendingCount, color = NomoWarmAmber)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onSyncNowClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NomoTerracotta)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = NomoCream)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync All Memories to Drive Now", fontWeight = FontWeight.Bold, color = NomoCream)
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
                                text = "NOMO stores your photos and location history exclusively in your local Room database and your personal Google Drive. No central company database ever sees your personal life map.",
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
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text = "$count", fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NomoDeepCharcoal)
    }
}
