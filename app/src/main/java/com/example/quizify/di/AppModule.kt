package com.example.quizify.di

import android.content.Context
import com.example.quizify.data.repository.QuizRepository
import com.example.quizify.domain.ai.GeminiApiService
import com.example.quizify.utils.DocumentParser
import com.example.quizify.utils.TextSummarizer
import com.example.quizify.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGroqApi(): GeminiApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")  // Grok ap key
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(api: GeminiApiService): QuizRepository {
        return QuizRepository(api)
    }

    @Provides
    @Singleton
    fun provideDocumentParser(@ApplicationContext context: android.content.Context): DocumentParser {
        return DocumentParser(context)
    }

    @Provides
    @Singleton
    fun provideTextSummarizer(): TextSummarizer {
        return TextSummarizer()
    }


    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences {
        return UserPreferences(context)
    }
}