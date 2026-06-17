package com.example.quizify.ui.navhost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizify.ui.screens.*
import com.example.quizify.ui.theme.BackgroundDark
import com.example.quizify.ui.theme.PrimaryPurple
import com.example.quizify.ui.theme.TextPrimary
import com.example.quizify.ui.viewModel.QuizViewModel
import androidx.compose.material3.Text

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object UploadConfig : Screen("upload_config")
    data object Loading : Screen("loading")
    data object Quiz : Screen("quiz")
    data object Result : Screen("result")
}

@Composable
fun QuizNavHost() {
    val navController = rememberNavController()
    val viewModel: QuizViewModel = hiltViewModel()

    val hasSeenSplash by viewModel.hasSeenSplash.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val quizResult by viewModel.quizResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val config by viewModel.config.collectAsState()
    if (hasSeenSplash == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ){
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryPurple
            )
        }
        return
    }
    val startDestination = if (hasSeenSplash == true) {
        Screen.UploadConfig.route
    } else {
        Screen.Splash.route
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onGetStarted = {
                    viewModel.markSplashSeen()
                    navController.navigate(Screen.UploadConfig.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.UploadConfig.route) {
            UploadConfigScreen(
                onGenerateQuiz = { config ->
                    viewModel.setConfig(config)
                    val uri = android.net.Uri.parse(config.documentUri)
                    viewModel.processDocumentAndGenerateQuestions(uri, config.pastedText)
                    navController.navigate(Screen.Loading.route)
                }
            )
        }

        composable(Screen.Loading.route) {

            LaunchedEffect(questions, error) {
                when {
                    error != null -> {
                    }
                    questions.isNotEmpty() -> {
                        navController.navigate(Screen.Quiz.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }
                }
            }
            when {
                error != null -> ErrorScreen(
                    error = error!!,
                    onRetry = {
                        viewModel.clearError()
                        navController.popBackStack()
                    }
                )
                else -> LoadingScreen()
            }
        }

        composable(Screen.Quiz.route) {
            if (questions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().background(BackgroundDark),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
                return@composable
            }

            QuizScreen(
                questions = questions,
                timeLimitMinutes = config?.timeLimitMinutes,
                onSubmit = { answers ->
                    viewModel.submitQuiz(answers)
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                },
                onExit = {
                    viewModel.resetQuiz()
                    navController.navigate(Screen.UploadConfig.route) {
                        popUpTo(Screen.UploadConfig.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            val result = quizResult
            if (result == null) {
                Box(
                    modifier = Modifier.fillMaxSize().background(BackgroundDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No result available", color = TextPrimary)
                }
                return@composable
            }

            ResultScreen(
                result = result,
                onRetake = {
                    viewModel.resetQuiz()
                    navController.navigate(Screen.UploadConfig.route) {
                        popUpTo(Screen.UploadConfig.route) { inclusive = true }
                    }
                },
                onNewQuiz = {
                    viewModel.resetQuiz()
                    navController.navigate(Screen.UploadConfig.route) {
                        popUpTo(Screen.UploadConfig.route) { inclusive = true }
                    }
                }
            )
        }
    }
}