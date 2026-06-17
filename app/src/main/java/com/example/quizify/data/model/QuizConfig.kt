package com.example.quizify.data.model

data class QuizConfig(
    val documentUri: String,
    val examType: ExamType,
    val customExamName: String? = null,
    val questionCount: Int = 25,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val timeLimitMinutes: Int? = null,  // null = no timer
    val selectedChapters: List<String>? = null,  // For large files
    val pastedText: String? = null
)

enum class ExamType {
    BANKING, SSC, RRB, TNPSC, UPSC, OTHER
}