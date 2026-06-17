package com.example.quizify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import com.example.quizify.ui.navhost.QuizNavHost
import com.example.quizify.ui.theme.BackgroundDark
import com.example.quizify.ui.theme.GradientStart
import com.example.quizify.ui.theme.QuizGenieTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(0xFF0F0F23.toInt())
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizGenieTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = BackgroundDark,        // your dark background color
                        darkIcons = false              // white icons on dark bg
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizNavHost()
                }
            }
        }
    }
}