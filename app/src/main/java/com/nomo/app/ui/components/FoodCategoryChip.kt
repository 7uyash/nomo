package com.nomo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.nomo.app.ui.theme.*

@Composable
fun FoodCategoryChip(
    category: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (emoji, chipColor) = when (category) {
        "Food" -> "🍕" to NomoTerracotta
        "Cafe" -> "☕" to NomoWarmAmber
        "Travel" -> "✈️" to NomoSage
        "Event" -> "🎉" to NomoEventRose
        "Shopping" -> "🛒" to NomoWarmAmber
        "Landmark" -> "🏛️" to NomoLandmarkPurple
        "Personal" -> "⭐" to NomoTerracotta
        else -> "📍" to NomoDeepCharcoal
    }

    val backgroundColor = if (isSelected) chipColor else NomoSurface
    val textColor = if (isSelected) NomoCream else NomoDeepCharcoal
    val borderColor = if (isSelected) chipColor else NomoCardBorder

    Row(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
