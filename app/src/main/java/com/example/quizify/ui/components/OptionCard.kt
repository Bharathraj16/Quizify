package com.example.quizify.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizify.ui.theme.BorderLight
import com.example.quizify.ui.theme.PrimaryPurple
import com.example.quizify.ui.theme.SurfaceDark
import com.example.quizify.ui.theme.TextMuted
import com.example.quizify.ui.theme.TextPrimary

@Composable
fun OptionCard(
    letter: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryPurple.copy(alpha = 0.15f) else Color.Transparent,
        label = "background"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryPurple else BorderLight,
        label = "border"
    )

    val letterBackground by animateColorAsState(
        targetValue = if (isSelected) PrimaryPurple else SurfaceDark,
        label = "letterBg"
    )

    val letterTextColor by animateColorAsState(
        targetValue = if (isSelected) TextPrimary else TextMuted,
        label = "letterText"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(letterBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = letterTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            color = TextPrimary,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}