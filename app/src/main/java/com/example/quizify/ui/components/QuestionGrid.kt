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
import com.example.quizify.ui.theme.SuccessGreen
import com.example.quizify.ui.theme.SurfaceDark
import com.example.quizify.ui.theme.TextMuted
import com.example.quizify.ui.theme.TextPrimary

@Composable
fun QuestionGrid(
    totalQuestions: Int,
    currentIndex: Int,
    answeredIndices: List<Int>,
    onQuestionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Question Navigator",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val columns = 5
        val rows = (totalQuestions + columns - 1) / columns

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < totalQuestions) {
                        GridItem(
                            number = index + 1,
                            isCurrent = index == currentIndex,
                            isAnswered = index in answeredIndices,
                            onClick = { onQuestionClick(index) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (row < rows - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun GridItem(
    number: Int,
    isCurrent: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCurrent -> PrimaryPurple
            isAnswered -> SuccessGreen.copy(alpha = 0.2f)
            else -> SurfaceDark
        },
        label = "gridBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isCurrent -> PrimaryPurple
            isAnswered -> SuccessGreen
            else -> BorderLight
        },
        label = "gridBorder"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isCurrent -> TextPrimary
            isAnswered -> SuccessGreen
            else -> TextMuted
        },
        label = "gridText"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}