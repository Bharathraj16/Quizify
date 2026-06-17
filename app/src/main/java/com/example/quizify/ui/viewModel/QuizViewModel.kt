package com.example.quizify.ui.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizify.data.model.*
import com.example.quizify.data.repository.QuizRepository
import com.example.quizify.utils.DocumentParser
import com.example.quizify.utils.PerformanceTips
import com.example.quizify.utils.TextSummarizer
import com.example.quizify.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val documentParser: DocumentParser,
    private val textSummarizer: TextSummarizer,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _hasSeenSplash = MutableStateFlow<Boolean?>(null) // null = loading
    val hasSeenSplash: StateFlow<Boolean?> = _hasSeenSplash.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _quizResult = MutableStateFlow<QuizResult?>(null)
    val quizResult: StateFlow<QuizResult?> = _quizResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _config = MutableStateFlow<QuizConfig?>(null)

    private val _documentText = MutableStateFlow("")
    private val _isLargeDocument = MutableStateFlow(false)
    val config: StateFlow<QuizConfig?> = _config.asStateFlow()
    private var quizStartTime: Long = 0
    init {
        viewModelScope.launch {
            userPreferences.hasSeen.collect { seen ->
                _hasSeenSplash.value = seen
            }
        }
    }
    fun setConfig(config: QuizConfig) {
        _config.value = config
    }

    fun processDocumentAndGenerateQuestions(uri: Uri, pastedText: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val textForAI = if (!pastedText.isNullOrBlank()) {
                    // ✅ Paste mode — skip document parsing entirely
                    Log.d("QuizViewModel", "Using pasted text: ${pastedText.length} chars")
                    textSummarizer.trimToTokenBudget(pastedText)
                } else {
                    // ✅ PDF/File mode — same as before
                    val parsedDoc = documentParser.parseDocument(uri)
                    if (parsedDoc.isLargeDocument)
                        textSummarizer.summarizeForAI(parsedDoc.fullText)
                    else
                        parsedDoc.fullText
                }

                _documentText.value = textForAI
                generateQuestionsFromText(textForAI)

            } catch (e: Exception) {
                _error.value = "Failed to process: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun markSplashSeen() {
        viewModelScope.launch {
            userPreferences.markSplashSeen()
        }
    }

    fun generateQuestionsFromText(text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val config = _config.value ?: return@launch

            val result = quizRepository.generateQuestionsFromText(
                text = text,
                examType = config.examType,
                customExamName = config.customExamName,
                questionCount = config.questionCount,
                difficulty = config.difficulty
            )

            result.onSuccess { questions ->
                _questions.value = questions
                quizStartTime = System.currentTimeMillis()
                _isLoading.value = false
            }.onFailure { error ->
                _error.value = "Failed to generate questions: ${error.message}"
                _isLoading.value = false
            }
        }
    }

    fun submitQuiz(answers: List<String?>) {
        val questions = _questions.value
        if (questions.isEmpty()) return

        val timeTaken = (System.currentTimeMillis() - quizStartTime) / 1000

        var correct = 0
        var wrong = 0
        var skipped = 0
        val questionResults = mutableListOf<QuestionResult>()
        val weakTopics = mutableSetOf<String>()

        questions.forEachIndexed { index, question ->
            val userAnswer = answers.getOrNull(index)
            val isCorrect = userAnswer == question.correctAnswer
            val isSkipped = userAnswer == null

            when {
                isSkipped -> skipped++
                isCorrect -> correct++
                else -> {
                    wrong++
                    question.topic?.let { weakTopics.add(it) }
                }
            }

            questionResults.add(
                QuestionResult(
                    question = question,
                    userAnswer = userAnswer,
                    isCorrect = isCorrect,
                    isSkipped = isSkipped
                )
            )
        }

        val total = questions.size
        val score = if (total > 0) (correct.toFloat() / total) * 100 else 0f

        val result = QuizResult(
            totalQuestions = total,
            correctAnswers = correct,
            wrongAnswers = wrong,
            skippedAnswers = skipped,
            scorePercentage = score,
            timeTakenSeconds = timeTaken,
            questionResults = questionResults,
            aiTip = PerformanceTips.generateTip(
                QuizResult(total, correct, wrong, skipped, score, timeTaken, questionResults, "", weakTopics.toList())
            ),
            weakTopics = weakTopics.toList()
        )

        _quizResult.value = result
    }

    fun resetQuiz() {
        _questions.value = emptyList()
        _quizResult.value = null
        _error.value = null
        _config.value = null
        _documentText.value = ""
        _isLargeDocument.value = false
    }

    fun clearError() {
        _error.value = null
    }
}