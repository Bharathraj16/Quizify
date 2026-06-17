package com.example.quizify.data.model

data class DocumentChunk(
    val chapterName: String,
    val content: String,
    val pageRange: String,
    val isSelected: Boolean = true
)