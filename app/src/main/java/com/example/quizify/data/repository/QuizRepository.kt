package com.example.quizify.data.repository


import android.util.Log
import com.example.quizify.data.model.Difficulty
import com.example.quizify.data.model.ExamType
import com.example.quizify.data.model.GeneratedQuizResponse
import com.example.quizify.data.model.Question
import com.example.quizify.domain.ai.GeminiApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

import com.example.quizify.domain.ai.GrokMessage
import com.example.quizify.domain.ai.GrokRequest
import com.google.gson.annotations.SerializedName
import com.tom_roush.pdfbox.BuildConfig

data class QuestionDto(
    @SerializedName("question") val question: String = "",
    @SerializedName("options") val options: List<String> = emptyList(),
    @SerializedName("answer") val answer: String = "",
    @SerializedName("explanation") val explanation: String = "",
    @SerializedName("topic") val topic: String = ""
)

data class GeneratedQuizResponse(
    @SerializedName("questions") val questions: List<QuestionDto> = emptyList()
)

@Singleton
class QuizRepository @Inject constructor(
    private val groqApi: GeminiApiService
) {
    private val gson = Gson()


    private val API_KEY = BuildConfig.GROQ_API_KEY

    suspend fun generateQuestionsFromText(
        text: String,
        examType: ExamType,
        customExamName: String?,
        questionCount: Int,
        difficulty: Difficulty
    ): Result<List<Question>> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(text, examType, customExamName, questionCount, difficulty)

            val request = GrokRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(
                    GrokMessage(role = "system", content = "You are an expert exam question generator. Always respond with valid JSON only, no markdown."),
                    GrokMessage(role = "user", content = prompt)
                ),
                temperature = 0.7f,
                maxTokens = 8192
            )

            Log.d("QuizRepository", "Sending request to Groq API...")
            val response = groqApi.generateContent(API_KEY, request)

            val generatedText = response.choices.firstOrNull()?.message?.content
                ?: return@withContext Result.failure(Exception("Empty response from Groq"))

            Log.d("QuizRepository", "Response received: ${generatedText.take(200)}...")

            val quizResponse = parseResponse(generatedText)
            val questions = quizResponse.questions.mapIndexed { index, dto ->
                Question(
                    id = index,
                    question = dto.question,
                    options = dto.options.shuffled(),
                    correctAnswer = dto.answer,
                    explanation = dto.explanation,
                    topic = dto.topic,
                    difficulty = difficulty
                )
            }

            Result.success(questions)

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("QuizRepository", "FULL ERROR: $errorBody")
            Result.failure(Exception("API Error ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error generating questions", e)
            Result.failure(e)
        }
    }

    private fun buildPrompt(
        text: String,
        examType: ExamType,
        customExamName: String?,
        questionCount: Int,
        difficulty: Difficulty
    ): String {
        val examName = customExamName ?: examType.name
        val maxChars = 6000
        val trimmedText = if (text.length > maxChars) {
            text.take(maxChars) + "\n\n[Text trimmed for processing...]"
        } else {
            text
        }
        return """
You are an expert exam question generator for Indian competitive exams.

TASK: Generate $questionCount multiple-choice questions based on the provided text.

EXAM TYPE: $examName
DIFFICULTY: ${difficulty.name}

RULES:
1. Each question must have exactly 4 options
2. Only ONE correct answer per question  
3. Questions should match $examName exam pattern
4. Return ONLY valid JSON, no markdown, no extra text
5. IGNORE any author names, company names, publisher info, or metadata
6. ONLY generate questions about the ACTUAL SUBJECT CONTENT of the document

SOURCE TEXT:
$trimmedText

RESPONSE FORMAT:
{
  "questions": [
    {
      "question": "Question text here?",
      "options": ["Option A", "Option B", "Option C", "Option D"],
      "answer": "Exact text of correct option",
      "explanation": "Why this is correct",
      "topic": "Topic category"
    }
  ]
}
        """.trimIndent()
    }

    private fun parseResponse(text: String): GeneratedQuizResponse {
        val cleanJson = text
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            gson.fromJson(cleanJson, GeneratedQuizResponse::class.java)
        } catch (e: Exception) {
            val jsonStart = cleanJson.indexOf('{')
            val jsonEnd = cleanJson.lastIndexOf('}') + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                gson.fromJson(
                    cleanJson.substring(jsonStart, jsonEnd),
                    GeneratedQuizResponse::class.java
                )
            } else {
                GeneratedQuizResponse()
            }
        }
    }
}