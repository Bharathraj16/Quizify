package com.example.quizify.domain.ai

import com.example.quizify.data.model.Question
import com.example.quizify.data.model.QuizConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject

class GeminiQuestionGenerator @Inject constructor(
    private val api: GeminiApi
) {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    suspend fun generateQuestions(
        text: String,
        config: QuizConfig
    ): List<Question> = withContext(Dispatchers.IO) {

        val prompt = buildString {
            appendLine("You are an expert exam question generator for Indian competitive exams.")
            appendLine("Generate ${config.questionCount} multiple-choice questions based on the following text.")
            appendLine("Exam Type: ${config.examType}")
            appendLine("Difficulty: ${config.difficulty}")
            appendLine("")
            appendLine("RULES:")
            appendLine("1. Each question must have exactly 4 options (A, B, C, D)")
            appendLine("2. Only ONE correct answer per question")
            appendLine("3. Questions should be relevant to ${config.examType} exam pattern")
            appendLine("4. Include questions on: facts, concepts, dates, definitions, and applications")
            appendLine("5. Return ONLY valid JSON array format")
            appendLine("")
            appendLine("JSON FORMAT:")
            appendLine("""[
              {
                "question": "Question text here?",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "answer": "Exact text of correct option",
                "explanation": "Brief explanation of why this is correct",
                "topic": "Topic category"
              }
            ]""")
            appendLine("")
            appendLine("SOURCE TEXT:")
            appendLine(text.take(25000))  // Gemini has token limit
        }

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 8192
            )
        )

        val response = api.generateContent(apiKey, request)
        parseQuestionsFromResponse(response, config)
    }

    private fun parseQuestionsFromResponse(
        response: GeminiResponse,
        config: QuizConfig
    ): List<Question> {
        val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response")

        val jsonStr = text.substringAfter("```json").substringBefore("```")
            .ifEmpty { text.substringAfter("[").substringBeforeLast("]") }
            .let { "[$it]" }

        val type = object : TypeToken<List<QuestionDto>>() {}.type
        val questions: List<QuestionDto> = Gson().fromJson(jsonStr, type)

        return questions.mapIndexed { index, dto ->
            Question(
                id = index,
                question = dto.question,
                options = dto.options.shuffled(),
                correctAnswer = dto.answer,
                explanation = dto.explanation,
                topic = dto.topic,
                difficulty = config.difficulty
            )
        }
    }
}


data class QuestionDto(
    val question: String,
    val options: List<String>,
    val answer: String,
    val explanation: String?,
    val topic: String?
)


interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}


data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig
)

data class Content(val parts: List<Part>)
data class Part(val text: String)
data class GenerationConfig(val temperature: Double, val maxOutputTokens: Int)
data class GeminiResponse(val candidates: List<Candidate>)
data class Candidate(val content: Content)