package com.example.quizify.data.model

import com.google.gson.annotations.SerializedName

data class Question(
    val id: Int = 0,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String? = null,
    val topic: String? = null,
    val difficulty: Difficulty = Difficulty.MEDIUM
)


enum class Difficulty { EASY, MEDIUM, HARD, MIXED }


data class GeneratedQuizResponse(
    @SerializedName("questions") val questions: List<QuestionDto> = emptyList()
)

data class QuestionDto(
    @SerializedName("question") val question: String = "",
    @SerializedName("options") val options: List<String> = emptyList(),
    @SerializedName("answer") val answer: String = "",
    @SerializedName("explanation") val explanation: String? = null,
    @SerializedName("topic") val topic: String? = null
)

data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig = GenerationConfig()
)

data class Content(val parts: List<Part>)
data class Part(val text: String)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val maxOutputTokens: Int = 8192,
    val responseMimeType: String = "application/json"
)

data class GeminiResponse(
    val candidates: List<Candidate> = emptyList()
)

data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null
)
