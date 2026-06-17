package com.example.quizify.data.model

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedAnswers: Int,
    val scorePercentage: Float,
    val timeTakenSeconds: Long,
    val questionResults: List<QuestionResult>,
    val aiTip: String,
    val weakTopics: List<String>
)

data class QuestionResult(
    val question: Question,
    val userAnswer: String?,
    val isCorrect: Boolean,
    val isSkipped: Boolean = false
)
