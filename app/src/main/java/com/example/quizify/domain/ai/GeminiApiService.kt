package com.example.quizify.domain.ai

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class GrokRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<GrokMessage>,
    @SerializedName("temperature") val temperature: Float = 0.7f,
    @SerializedName("max_tokens") val maxTokens: Int = 8192
)

data class GrokMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)


data class GrokResponse(
    @SerializedName("choices") val choices: List<GrokChoice> = emptyList()
)

data class GrokChoice(
    @SerializedName("message") val message: GrokMessage
)

interface GeminiApiService {
    @POST("chat/completions")
    suspend fun generateContent(
        @Header("Authorization") apiKey: String,
        @Body request: GrokRequest
    ): GrokResponse
}
