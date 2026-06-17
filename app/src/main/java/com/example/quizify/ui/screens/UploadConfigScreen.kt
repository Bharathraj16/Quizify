package com.example.quizify.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.quizify.data.model.Difficulty
import com.example.quizify.data.model.ExamType
import com.example.quizify.data.model.QuizConfig
import com.example.quizify.ui.theme.BackgroundDark
import com.example.quizify.ui.theme.BorderLight
import com.example.quizify.ui.theme.PrimaryPurple
import com.example.quizify.ui.theme.SecondaryViolet
import com.example.quizify.ui.theme.SuccessGreen
import com.example.quizify.ui.theme.SurfaceDark
import com.example.quizify.ui.theme.TextMuted
import com.example.quizify.ui.theme.TextPrimary

@Composable
fun UploadConfigScreen(
    onGenerateQuiz: (QuizConfig) -> Unit
) {
    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var pastedText by remember { mutableStateOf("") }
    var inputMode by remember { mutableStateOf(0) }
    var examType by remember { mutableStateOf(ExamType.BANKING) }
    var customExam by remember { mutableStateOf("") }
    var questionCount by remember { mutableIntStateOf(25) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var timeLimit by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFile = it
            fileName = it.lastPathSegment ?: "Unknown file"
        }
    }
    val isInputReady = (inputMode == 0 && selectedFile != null) ||
            (inputMode == 1 && pastedText.trim().length > 50)

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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Configure Quiz",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Set up your perfect quiz",
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        HorizontalDivider(color = BorderLight, thickness = 0.5.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("📄 Upload File", "📋 Paste Text").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (inputMode == index) PrimaryPurple else Color.Transparent)
                            .clickable { inputMode = index }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (inputMode == index) TextPrimary else TextMuted,
                            fontSize = 14.sp,
                            fontWeight = if (inputMode == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (inputMode == 0) {
                UploadArea(
                    selectedFile = selectedFile,
                    fileName = fileName,
                    onClick = { filePicker.launch("*/*") }
                )
            } else {
                OutlinedTextField(
                    value = pastedText,
                    onValueChange = { pastedText = it },
                    placeholder = {
                        Text(
                            "Paste your content here...\n\nE.g. Copy from ChatGPT, Grok, any AI or website and paste here.",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 350.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 20
                )

                val wordCount = pastedText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
                Text(
                    text = if (pastedText.isBlank()) "Minimum 50 words needed"
                    else "$wordCount words pasted ✓",
                    color = if (wordCount > 50) SuccessGreen else TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Exam Type")
            Spacer(modifier = Modifier.height(12.dp))
            ExamTypeGrid(selected = examType, onSelect = { examType = it })

            AnimatedVisibility(visible = examType == ExamType.OTHER, enter = expandVertically() + fadeIn()) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customExam,
                        onValueChange = { customExam = it },
                        placeholder = { Text("Enter exam name (e.g., NEET, JEE)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Questions: $questionCount")
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = questionCount.toFloat(),
                onValueChange = { questionCount = it.toInt() },
                valueRange = 5f..100f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryPurple,
                    activeTrackColor = PrimaryPurple,
                    inactiveTrackColor = SurfaceDark
                )
            )
            Text(
                text = "Recommended: 25-50 for best results",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Difficulty Level")
            Spacer(modifier = Modifier.height(12.dp))
            DifficultyChips(selected = difficulty, onSelect = { difficulty = it })

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Time Limit (Optional)")
            Spacer(modifier = Modifier.height(12.dp))
            TimerChips(selected = timeLimit, onSelect = { timeLimit = it })

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!isInputReady) return@Button
                    isLoading = true

                    if (inputMode == 0) {
                        selectedFile?.let { uri ->
                            onGenerateQuiz(
                                QuizConfig(
                                    documentUri = uri.toString(),
                                    examType = examType,
                                    customExamName = customExam.takeIf { it.isNotBlank() },
                                    questionCount = questionCount,
                                    difficulty = difficulty,
                                    timeLimitMinutes = timeLimit
                                )
                            )
                        }
                    } else {
                        onGenerateQuiz(
                            QuizConfig(
                                documentUri = "paste://${pastedText.take(100)}", // marker
                                examType = examType,
                                customExamName = customExam.takeIf { it.isNotBlank() },
                                questionCount = questionCount,
                                difficulty = difficulty,
                                timeLimitMinutes = timeLimit,
                                pastedText = pastedText
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = isInputReady && !isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isInputReady)
                                Brush.horizontalGradient(listOf(PrimaryPurple, SecondaryViolet))
                            else
                                Brush.horizontalGradient(listOf(SurfaceDark, SurfaceDark)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TextPrimary)
                            Text(text = "Generate Quiz", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
}

@Composable
private fun UploadArea(selectedFile: Uri?, fileName: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, if (selectedFile != null) SuccessGreen else PrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .background(if (selectedFile != null) SuccessGreen.copy(alpha = 0.05f) else PrimaryPurple.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedFile == null) {
            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload", tint = PrimaryPurple, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "Tap to Upload", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "PDF, DOCX, TXT (Max 50MB)", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        } else {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "File selected", tint = SuccessGreen, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = fileName, color = SuccessGreen, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            Text(text = "Tap to change file", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun ExamTypeGrid(selected: ExamType, onSelect: (ExamType) -> Unit) {
    val exams = listOf(
        Triple(ExamType.BANKING, "🏦", "Banking"),
        Triple(ExamType.SSC, "🚂", "SSC"),
        Triple(ExamType.RRB, "🛤️", "RRB"),
        Triple(ExamType.TNPSC, "🏛️", "TNPSC"),
        Triple(ExamType.UPSC, "🎓", "UPSC"),
        Triple(ExamType.OTHER, "✏️", "Other")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (row in exams.chunked(2)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for ((type, icon, name) in row) {
                    ExamCard(icon = icon, name = name, isSelected = selected == type, onClick = { onSelect(type) }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ExamCard(icon: String, name: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PrimaryPurple.copy(alpha = 0.15f) else SurfaceDark)
            .border(1.5.dp, if (isSelected) PrimaryPurple else BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, color = if (isSelected) TextPrimary else TextMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DifficultyChips(selected: Difficulty, onSelect: (Difficulty) -> Unit) {
    val difficulties = listOf(Difficulty.EASY to "Easy", Difficulty.MEDIUM to "Medium", Difficulty.HARD to "Hard", Difficulty.MIXED to "Mixed")
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        for ((diff, label) in difficulties) {
            Chip(text = label, isSelected = selected == diff, onClick = { onSelect(diff) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TimerChips(selected: Int?, onSelect: (Int?) -> Unit) {
    val options = listOf(null to "No Timer", 15 to "15 min", 30 to "30 min", 60 to "1 hour")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        for ((minutes, label) in options) {
            Chip(text = label, isSelected = selected == minutes, onClick = { onSelect(minutes) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun Chip(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .background(if (isSelected) PrimaryPurple else SurfaceDark)
            .border(1.dp, if (isSelected) PrimaryPurple else BorderLight, RoundedCornerShape(25.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = if (isSelected) TextPrimary else TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
