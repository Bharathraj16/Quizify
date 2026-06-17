package com.example.quizify.utils

import com.example.quizify.data.model.QuizResult

// PerformanceTips.kt
object PerformanceTips {

    fun generateTip(result: QuizResult): String {
        val tips = mutableListOf<String>()

        // Speed analysis
        val avgTimePerQuestion = result.timeTakenSeconds / result.totalQuestions
        when {
            avgTimePerQuestion < 30 -> tips.add("🔥 Excellent speed! You're answering very quickly.")
            avgTimePerQuestion < 60 -> tips.add("⚡ Good pace! Try to reduce time per question slightly.")
            else -> tips.add("⏱️ Work on your speed. Try timed practice tests daily.")
        }

        // Accuracy analysis
        when {
            result.scorePercentage >= 80 -> tips.add("🎯 Outstanding accuracy! You're exam-ready!")
            result.scorePercentage >= 60 -> tips.add("👍 Good accuracy! Focus on your weak topics to reach 80%+.")
            else -> tips.add("📚 Focus on understanding concepts before speed. Review fundamentals.")
        }

        // Weak topics
        if (result.weakTopics.isNotEmpty()) {
            tips.add("📖 Priority topics to study: ${result.weakTopics.take(3).joinToString(", ")}")
        }

        // Skipped questions
        if (result.skippedAnswers > result.totalQuestions * 0.2) {
            tips.add("⚠️ You're skipping too many questions. Practice elimination techniques.")
        }

        // Wrong answer pattern
        if (result.wrongAnswers > result.correctAnswers) {
            tips.add("💡 Try reading questions twice before answering. Don't rush!")
        }

        return tips.joinToString("\n\n")
    }

    fun getMotivationalMessage(score: Float): String = when {
        score >= 90 -> "🏆 Legendary! You're in the top percentile!"
        score >= 80 -> "🌟 Excellent! Keep this momentum going!"
        score >= 70 -> "💪 Very Good! A little more practice and you'll ace it!"
        score >= 60 -> "👍 Good effort! Focus on weak areas."
        score >= 50 -> "📈 Keep pushing! Consistency is key."
        score >= 40 -> "🎯 Don't give up! Every attempt makes you stronger."
        else -> "💪 Every expert was once a beginner. Keep practicing!"
    }
}