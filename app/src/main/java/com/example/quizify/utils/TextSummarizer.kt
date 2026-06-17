package com.example.quizify.utils

import com.example.quizify.data.model.DocumentChunk
import javax.inject.Inject

// TextSummarizer.kt
class TextSummarizer @Inject constructor() {

    // For 200-300 page documents, we need smart chunking
    fun processLargeDocument(document: ParsedDocument): LargeDocumentResult {
        val chunks = mutableListOf<DocumentChunk>()

        // Strategy 1: Extract chapters/sections using regex patterns
        val chapterPattern = Regex(
            """(?i)(?:chapter|unit|section|topic|module)\s*[\dIVX]+[.:]?\s*([^\n]+)"""
        )

        val matches = chapterPattern.findAll(document.fullText)
        val chapterPositions = matches.map { it.range.first }.toList()

        if (chapterPositions.size >= 3) {
            // Document has clear chapters
            for (i in chapterPositions.indices) {
                val start = chapterPositions[i]
                val end = if (i + 1 < chapterPositions.size) chapterPositions[i + 1] else document.fullText.length
                val content = document.fullText.substring(start, end)
                val chapterName = matches.elementAt(i).groupValues[1].trim()

                chunks.add(DocumentChunk(
                    chapterName = chapterName,
                    content = content.take(5000),  // Limit per chunk
                    pageRange = "Pages ${estimatePageRange(i, document.totalPages, chapterPositions.size)}"
                ))
            }
        } else {
            // Strategy 2: Sliding window chunking for unstructured docs
            val words = document.fullText.split("\\s+".toRegex())
            val chunkSize = 2000  // words per chunk
            var currentChunk = 0

            for (i in words.indices step chunkSize) {
                val chunkWords = words.subList(i, minOf(i + chunkSize, words.size))
                val chunkText = chunkWords.joinToString(" ")

                // Extract key sentence as chapter name
                val firstSentence = chunkText.split(".")[0].take(50)

                chunks.add(DocumentChunk(
                    chapterName = "Part ${currentChunk + 1}: $firstSentence...",
                    content = chunkText,
                    pageRange = "Pages ${i / 500 + 1}-${(i + chunkSize) / 500 + 1}"
                ))
                currentChunk++
            }
        }

        return LargeDocumentResult(
            chunks = chunks,
            totalChunks = chunks.size,
            estimatedTokens = document.fullText.length / 4  // Rough estimate
        )
    }

    // Extract most important content for AI (when token limit is reached)
    fun extractKeyContent(text: String, maxChars: Int = 25000): String {
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))

        // Score sentences by importance
        val scoredSentences = sentences.map { sentence ->
            val score = calculateImportanceScore(sentence)
            sentence to score
        }.sortedByDescending { it.second }

        // Take top sentences until we hit the limit
        val selectedSentences = mutableListOf<String>()
        var currentLength = 0

        for ((sentence, _) in scoredSentences) {
            if (currentLength + sentence.length > maxChars) break
            selectedSentences.add(sentence)
            currentLength += sentence.length
        }

        // Reconstruct in original order
        return sentences.filter { it in selectedSentences }.joinToString(" ")
    }

    fun summarizeForAI(text: String, maxChars: Int = 25000): String {
        return extractKeyContent(text, maxChars)
    }

    fun trimToTokenBudget(text: String, maxChars: Int = 6000): String {
        if (text.length <= maxChars) return text

        // Smart trim — keep beginning and end (most important parts)
        val halfChars = maxChars / 2
        val beginning = text.take(halfChars)
        val ending = text.takeLast(halfChars / 2)

        return "$beginning\n\n...[middle section trimmed]...\n\n$ending"
    }

    private fun calculateImportanceScore(sentence: String): Int {
        var score = 0

        // Keywords that indicate important information
        val importantKeywords = listOf(
            "definition", "means", "is called", "refers to",
            "established", "founded", "launched", "introduced",
            "important", "significant", "key", "crucial",
            "first", "largest", "highest", "only",
            "RBI", "SEBI", "NABARD", "IBPS", "GDP", "NPA",
            "act", "policy", "scheme", "program",
            "committee", "report", "recommendation"
        )

        importantKeywords.forEach { keyword ->
            if (sentence.contains(keyword, ignoreCase = true)) score += 3
        }

        // Numbers and dates are usually important
        if (sentence.contains(Regex("\\d{4}"))) score += 2  // Year
        if (sentence.contains(Regex("\\d+%"))) score += 2  // Percentage
        if (sentence.contains(Regex("Rs\\.?\\s*\\d+"))) score += 2  // Money

        // Longer sentences with multiple facts score higher
        score += sentence.split(",").size

        return score
    }

    private fun estimatePageRange(index: Int, totalPages: Int, totalChunks: Int): String {
        val pagesPerChunk = totalPages / totalChunks
        val start = index * pagesPerChunk + 1
        val end = minOf((index + 1) * pagesPerChunk, totalPages)
        return "$start-$end"
    }
}

data class LargeDocumentResult(
    val chunks: List<DocumentChunk>,
    val totalChunks: Int,
    val estimatedTokens: Int
)