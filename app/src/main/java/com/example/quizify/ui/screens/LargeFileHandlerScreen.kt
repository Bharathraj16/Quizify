package com.example.quizify.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.quizify.data.model.DocumentChunk
import com.example.quizify.ui.theme.BackgroundCard
import com.example.quizify.ui.theme.BackgroundDark
import com.example.quizify.ui.theme.BorderLight
import com.example.quizify.ui.theme.PrimaryPurple
import com.example.quizify.ui.theme.SecondaryViolet
import com.example.quizify.ui.theme.SuccessGreen
import com.example.quizify.ui.theme.SurfaceDark
import com.example.quizify.ui.theme.TextMuted
import com.example.quizify.ui.theme.TextPrimary
import com.example.quizify.ui.theme.WarningYellow

@Composable
fun LargeFileHandlerScreen(
    totalPages: Int,
    chunks: List<DocumentChunk>,
    onGenerate: (List<String>) -> Unit,
    onCancel: () -> Unit
) {
    var selectedChunks by remember { mutableStateOf(chunks.map { it.chapterName }) }
    var isGenerating by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "📄 Large File Detected",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Your file has $totalPages pages. Here's how we'll handle it:",
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        InfoCard(
            icon = Icons.Default.Psychology,
            iconColor = PrimaryPurple,
            title = "Smart Summarization",
            description = "AI will extract key concepts, important dates, and definitions from all pages automatically."
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            icon = Icons.Default.Layers,
            iconColor = SuccessGreen,
            title = "Chapter-wise Selection",
            description = "Choose specific chapters or topics you want to focus on."
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            icon = Icons.Default.Lightbulb,
            iconColor = WarningYellow,
            title = "Exam-Focused Filtering",
            description = "Prioritize content relevant to your selected exam (Banking/SSC/etc)."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "📋 Detected Chapters (${chunks.size} found)",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        chunks.forEach { chunk ->
            ChapterItem(
                chunk = chunk,
                isSelected = chunk.chapterName in selectedChunks,
                onToggle = {
                    selectedChunks = if (chunk.chapterName in selectedChunks) {
                        selectedChunks - chunk.chapterName
                    } else {
                        selectedChunks + chunk.chapterName
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryPurple.copy(alpha = 0.08f))
                .border(1.dp, PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Estimated Processing: ~45 seconds for ${selectedChunks.size} chapters",
                    color = PrimaryPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isGenerating = true
                onGenerate(selectedChunks)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            enabled = selectedChunks.isNotEmpty() && !isGenerating
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (selectedChunks.isNotEmpty()) Brush.horizontalGradient(listOf(PrimaryPurple, SecondaryViolet))
                        else Brush.horizontalGradient(listOf(SurfaceDark, SurfaceDark)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TextPrimary)
                        Text(
                            text = "Generate from ${selectedChunks.size} Chapters",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
        ) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(text = title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = description,
                color = TextMuted,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ChapterItem(
    chunk: DocumentChunk,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SuccessGreen.copy(alpha = 0.08f) else SurfaceDark)
            .border(
                1.5.dp,
                if (isSelected) SuccessGreen.copy(alpha = 0.4f) else BorderLight,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggle)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = SuccessGreen,
                uncheckedColor = TextMuted,
                checkmarkColor = BackgroundDark
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chunk.chapterName,
                color = if (isSelected) TextPrimary else TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = chunk.pageRange,
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = SuccessGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}