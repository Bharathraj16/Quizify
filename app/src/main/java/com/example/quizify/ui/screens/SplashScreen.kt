package com.example.quizify.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0F23), Color(0xFF1A1A3E))
                )
            )
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.15f))

        val infiniteTransition = rememberInfiniteTransition(label = "logo")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 }
        ) {
            Box(modifier = Modifier.scale(scale), contentAlignment = Alignment.Center) {
                Text(text = "🎯", fontSize = 80.sp, modifier = Modifier.padding(bottom = 20.dp))
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000, delayMillis = 300)) + slideInVertically(tween(1000, delayMillis = 300)) { it / 3 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "QuizGenie",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "AI-Powered Quiz Generator Upload any PDF, DOC, or Notes",
                            color = Color(0xFF8888AA),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1200, delayMillis = 600))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FeatureCard(
                    icon = Icons.Default.CloudUpload,
                    iconBg = Color(0xFF6366F1),
                    title = "Upload Document",
                    desc = "PDF, DOCX, TXT, or paste text"
                )
                FeatureCard(
                    icon = Icons.Default.Psychology,
                    iconBg = Color(0xFF10B981),
                    title = "AI Generates Questions",
                    desc = "Smart MCQs with options & answers"
                )
                FeatureCard(
                    icon = Icons.Default.TrendingUp,
                    iconBg = Color(0xFFF59E0B),
                    title = "Track & Improve",
                    desc = "Detailed analytics & personalized tips"
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.2f))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1400, delayMillis = 900)) + scaleIn(tween(1400, delayMillis = 900), initialScale = 0.8f)
        ) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Text(text = "Get Started", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A3E), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(iconBg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = desc, color = Color(0xFF8888AA), fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}