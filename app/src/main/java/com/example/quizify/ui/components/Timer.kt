package com.example.quizify.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import com.example.quizify.ui.theme.ErrorRed
import com.example.quizify.ui.theme.WarningYellow

@Composable
fun TimerBox(
    timeLeftSeconds: Int?,
    modifier: Modifier = Modifier
) {
    if (timeLeftSeconds == null || timeLeftSeconds < 0) return

    val isWarning = timeLeftSeconds < 300

    val backgroundColor by animateColorAsState(
        targetValue = if (isWarning) ErrorRed.copy(alpha = 0.15f) else WarningYellow.copy(alpha = 0.15f),
        label = "timerBg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isWarning) ErrorRed else WarningYellow,
        label = "timerText"
    )

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = "Timer",
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = timeString,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}