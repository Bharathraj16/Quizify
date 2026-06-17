package com.example.quizify.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.quizify.ui.theme.PrimaryPurple
import com.example.quizify.ui.theme.SecondaryViolet
import com.example.quizify.ui.theme.SurfaceDark


@Composable
fun QuizProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(PrimaryPurple, SecondaryViolet)
                    )
                )
        )
    }
}