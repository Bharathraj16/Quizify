package com.example.quizify.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream
import javax.inject.Inject

data class ParsedDocument(
    val fullText: String,
    val totalPages: Int,
    val isLargeDocument: Boolean
)

class DocumentParser @Inject constructor(
    private val context: Context
) {
    init {
        com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(context)
    }
    suspend fun parseDocument(uri: Uri): ParsedDocument = withContext(Dispatchers.IO) {
        val mimeType = context.contentResolver.getType(uri) ?: ""
        val path = uri.lastPathSegment ?: ""
        Log.d("DocumentParser", "File: $path, mimeType: $mimeType")

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open file")

        inputStream.use { stream ->
            when {
                mimeType.contains("pdf") || path.endsWith(".pdf", ignoreCase = true) -> parsePdf(stream)
                mimeType.contains("word") || mimeType.contains("officedocument") || path.endsWith(".docx", ignoreCase = true) -> parseDocx(stream)
                mimeType.contains("text") || mimeType.contains("plain") || path.endsWith(".txt", ignoreCase = true) -> parseTxt(stream)
                else -> throw Exception("Unsupported file. Please upload PDF, DOCX, or TXT.")
            }
        }
    }

    private fun parsePdf(inputStream: InputStream): ParsedDocument {
        Log.d("DocumentParser", "Parsing PDF...")

        return try {
            val document = com.tom_roush.pdfbox.pdmodel.PDDocument.load(inputStream)
            val stripper = com.tom_roush.pdfbox.text.PDFTextStripper()
            stripper.sortByPosition = true

            val rawText = stripper.getText(document)
            document.close()

            val cleanText = cleanExtractedText(
                rawText.replace(Regex("[^\\x20-\\x7E\\n\\r\\t]"), " ")
                    .replace(Regex(" {2,}"), " ")
            )

            val wordCount = cleanText.split(Regex("\\s+"))
                .filter { it.isNotEmpty() }.size
            val estimatedPages = (wordCount / 300).coerceAtLeast(1)

            Log.d("DocumentParser", "Extracted $wordCount words")

            ParsedDocument(
                fullText = cleanText,
                totalPages = estimatedPages,
                isLargeDocument = estimatedPages > 50
            )

        } catch (e: Exception) {
            Log.e("DocumentParser", "PDF parsing failed", e)
            ParsedDocument(
                fullText = "",
                totalPages = 0,
                isLargeDocument = false
            )
        }
    }

    private fun cleanExtractedText(rawText: String): String {
        val lines = rawText.lines().toMutableList()

        val meaningfulStart = lines.indexOfFirst { line ->
            val trimmed = line.trim()
            trimmed.length > 40 &&
                    !trimmed.matches(Regex(".*\\d{4}.*"))
        }.coerceAtLeast(0)

        val cleanLines = lines
            .drop(meaningfulStart)
            .filter { line ->
                val trimmed = line.trim()

                !trimmed.matches(Regex("(?i).*(pvt|ltd|inc|corp|company|author|written by|published|edition|copyright|©|all rights reserved|www\\.|http|@|version|isbn|doi).*")) &&
                        !trimmed.matches(Regex("^[\\d\\s.,-]+$")) &&
                        !trimmed.matches(Regex("^.{1,15}$")) &&
                        trimmed.isNotBlank()
            }

        return cleanLines.joinToString("\n")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }

    private fun isValidText(text: String): Boolean {
        if (text.length < 2) return false
        if (text.startsWith("/")) return false
        if (text.matches(Regex("^[0-9.]+$"))) return false
        if (text.matches(Regex("^[a-zA-Z]$"))) return false

        val letterCount = text.count { it.isLetter() }
        return letterCount >= text.length * 0.5
    }

    private fun parseDocx(inputStream: InputStream): ParsedDocument {
        Log.d("DocumentParser", "Parsing DOCX...")
        val text = StringBuilder()

        val document = XWPFDocument(inputStream)
        document.paragraphs.forEach { para ->
            if (para.text.isNotBlank()) {
                text.appendLine(para.text.trim())
            }
        }
        document.close()

        val resultText = text.toString().trim()
        val wordCount = resultText.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        val estimatedPages = (wordCount / 500).coerceAtLeast(1)

        return ParsedDocument(
            fullText = resultText,
            totalPages = estimatedPages,
            isLargeDocument = estimatedPages > 50
        )
    }

    private fun parseTxt(inputStream: InputStream): ParsedDocument {
        Log.d("DocumentParser", "Parsing TXT...")
        val text = inputStream.bufferedReader().use { it.readText() }
        val wordCount = text.split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        val estimatedPages = (wordCount / 500).coerceAtLeast(1)

        return ParsedDocument(
            fullText = text,
            totalPages = estimatedPages,
            isLargeDocument = estimatedPages > 50
        )
    }
}