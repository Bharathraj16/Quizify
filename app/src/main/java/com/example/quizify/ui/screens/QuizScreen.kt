package com.example.quizify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.quizify.data.model.Question
import com.example.quizify.ui.components.OptionCard
import com.example.quizify.ui.components.QuizProgressBar
import com.example.quizify.ui.components.TimerBox
import com.example.quizify.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    questions: List<Question>,
    timeLimitMinutes: Int?,
    onSubmit: (List<String?>) -> Unit,
    onExit: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var userAnswers by remember { mutableStateOf(List<String?>(questions.size) { null }) }
    var timeLeftSeconds by remember { mutableIntStateOf(timeLimitMinutes?.times(60) ?: -1) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showGridDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Timer
    LaunchedEffect(Unit) {
        if (timeLeftSeconds > 0) {
            while (timeLeftSeconds > 0) {
                delay(1000)
                timeLeftSeconds--
            }
            onSubmit(userAnswers)
        }
    }

    val currentQuestion = questions.getOrNull(currentIndex)
    if (currentQuestion == null) {
        onSubmit(userAnswers)
        return
    }

    LaunchedEffect(currentIndex) {
        scrollState.animateScrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundDark)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Q ${currentIndex + 1} / ${questions.size}",
                        color = PrimaryPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (timeLimitMinutes != null) {
                        TimerBox(timeLeftSeconds = timeLeftSeconds)
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(onClick = { showGridDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Questions Grid",
                            tint = TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit",
                            tint = TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            QuizProgressBar(progress = (currentIndex + 1).toFloat() / questions.size)
        }

        HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Question ${currentIndex + 1}",
                        color = PrimaryPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQuestion.question,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val letters = listOf("A", "B", "C", "D", "E", "F")
            currentQuestion.options.forEachIndexed { index, option ->
                OptionCard(
                    letter = letters.getOrElse(index) { "${index + 1}" },
                    text = option,
                    isSelected = userAnswers[currentIndex] == option,
                    onClick = {
                        userAnswers = userAnswers.toMutableList()
                            .apply { set(currentIndex, option) }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            val answeredCount = userAnswers.count { it != null }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$answeredCount of ${questions.size} answered",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundDark)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { if (currentIndex > 0) currentIndex-- },
                    enabled = currentIndex > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        disabledContainerColor = SurfaceDark.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (currentIndex < questions.size - 1) {
                            currentIndex++
                        } else {
                            onSubmit(userAnswers)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(PrimaryPurple, SecondaryViolet)),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (currentIndex < questions.size - 1) "Next" else "Submit",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            if (currentIndex < questions.size - 1) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Submit",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGridDialog) {
        AlertDialog(
            onDismissRequest = { showGridDialog = false },
            title = {
                Text(
                    "All Questions",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    // Legend
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(PrimaryPurple)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Current", color = TextMuted, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Answered", color = TextMuted, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SurfaceDark)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skipped", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    val columns = 5
                    val rows = (questions.size + columns - 1) / columns

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (row in 0 until rows) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (col in 0 until columns) {
                                    val index = row * columns + col
                                    if (index < questions.size) {
                                        val isCurrentQ = index == currentIndex
                                        val isAnswered = userAnswers[index] != null

                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    when {
                                                        isCurrentQ -> PrimaryPurple
                                                        isAnswered -> SuccessGreen
                                                        else -> SurfaceDark
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = when {
                                                    isCurrentQ || isAnswered -> Color.White
                                                    else -> TextMuted
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(44.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGridDialog = false }) {
                    Text("Close", color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = BackgroundCard,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Quiz?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Your progress will be lost. Are you sure?", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = { onExit() },
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                ) {
                    Text("Exit", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryPurple)
                ) {
                    Text("Continue", fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = BackgroundCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}