package com.nomo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomo.app.data.sync.SyncStatus
import com.nomo.app.ui.theme.*

@Composable
fun SyncStatusBadge(
    status: SyncStatus,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor, icon) = when (status) {
        SyncStatus.LOCAL_ONLY -> Quadruple("Saved Locally", NomoCream, SyncLocalGray, "🕒")
        SyncStatus.QUEUED -> Quadruple("Waiting to Sync", Color(0xFFFFF3E0), SyncQueuedAmber, "⏳")
        SyncStatus.UPLOADING -> Quadruple("Syncing to Drive", Color(0xE3E3F2FD), SyncUploadingBlue, "🔄")
        SyncStatus.SYNCED -> Quadruple("Synced", Color(0xFFE8F5E9), SyncSyncedGreen, "☁️")
        SyncStatus.UPLOAD_FAILED -> Quadruple("Sync Failed", Color(0xFFFFEBEE), SyncFailedRed, "⚠️")
        SyncStatus.RETRY_QUEUED -> Quadruple("Retry Queued", Color(0xFFFFF8E1), SyncQueuedAmber, "🔁")
    }

    Row(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
