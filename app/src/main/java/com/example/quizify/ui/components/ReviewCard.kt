package com.example.quizify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.RemoveCircle
import com.example.quizify.data.model.QuestionResult
import com.example.quizify.ui.theme.ErrorRed
import com.example.quizify.ui.theme.PrimaryPurple
import com.example.quizify.ui.theme.SuccessGreen
import com.example.quizify.ui.theme.TextPrimary
import com.example.quizify.ui.theme.TextSecondary
import com.example.quizify.ui.theme.WarningYellow

@Composable
fun ReviewCard(
    questionResult: QuestionResult,
    modifier: Modifier = Modifier
) {
    val (borderColor, backgroundColor, icon, iconColor) = when {
        questionResult.isSkipped -> listOf(
            WarningYellow, WarningYellow.copy(alpha = 0.08f),
            Icons.Default.RemoveCircle, WarningYellow
        )
        questionResult.isCorrect -> listOf(
            SuccessGreen, SuccessGreen.copy(alpha = 0.08f),
            Icons.Default.CheckCircle, SuccessGreen
        )
        else -> listOf(
            ErrorRed, ErrorRed.copy(alpha = 0.08f),
            Icons.Default.Cancel, ErrorRed
        )
    }.let { (a, b, c, d) -> Quad(a as Color, b as Color, c as ImageVector, d as Color) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(start = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Q${questionResult.question.id + 1}",
                color = PrimaryPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = questionResult.question.question,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (!questionResult.isSkipped) {
                AnswerRow(
                    icon = icon,
                    iconColor = if (questionResult.isCorrect) SuccessGreen else ErrorRed,
                    text = "Your Answer: ${questionResult.userAnswer}",
                    textColor = if (questionResult.isCorrect) SuccessGreen else ErrorRed
                )
            } else {
                AnswerRow(
                    icon = Icons.Default.RemoveCircle,
                    iconColor = WarningYellow,
                    text = "Skipped",
                    textColor = WarningYellow
                )
            }

            if (!questionResult.isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                AnswerRow(
                    icon = Icons.Default.CheckCircle,
                    iconColor = SuccessGreen,
                    text = "Correct: ${questionResult.question.correctAnswer}",
                    textColor = SuccessGreen
                )
            }

            questionResult.question.explanation?.let { explanation ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 $explanation",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun AnswerRow(
    icon: ImageVector,
    iconColor: Color,
    text: String,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)