package com.rakshakavach.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakshakavach.data.repository.GeminiRepository
import com.rakshakavach.domain.model.QuizQuestion
import com.rakshakavach.domain.repository.SafetyScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuizUiState {
    data class Loading(val taskName: String) : QuizUiState()
    data class QuizReady(
        val questions: List<QuizQuestion>,
        val currentIndex: Int = 0,
        val score: Int = 0,
        val answers: Map<Int, String> = emptyMap(),
        val selectedOption: String? = null
    ) : QuizUiState()
    data class QuizComplete(
        val finalScore: Int,
        val totalQuestions: Int,
        val questions: List<QuizQuestion>
    ) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val safetyScoreRepository: SafetyScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading(""))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(taskName: String) {
        _uiState.value = QuizUiState.Loading(taskName)
        viewModelScope.launch {
            try {
                var questions = geminiRepository.generateSafetyQuiz(taskName)
                if (questions.isEmpty()) {
                    questions = getFallbackQuestions()
                }
                _uiState.value = QuizUiState.QuizReady(questions = questions)
            } catch (e: Exception) {
                _uiState.value = QuizUiState.QuizReady(questions = getFallbackQuestions())
            }
        }
    }

    fun answerQuestion(option: String) {
        val currentState = _uiState.value
        if (currentState is QuizUiState.QuizReady && currentState.selectedOption == null) {
            val isCorrect = option == currentState.questions[currentState.currentIndex].correctAnswer
            val newScore = if (isCorrect) currentState.score + 1 else currentState.score
            val newAnswers = currentState.answers.toMutableMap().apply {
                put(currentState.currentIndex, option)
            }
            _uiState.value = currentState.copy(
                score = newScore,
                answers = newAnswers,
                selectedOption = option
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState is QuizUiState.QuizReady) {
            if (currentState.currentIndex + 1 < currentState.questions.size) {
                _uiState.value = currentState.copy(
                    currentIndex = currentState.currentIndex + 1,
                    selectedOption = null
                )
            } else {
                _uiState.value = QuizUiState.QuizComplete(
                    finalScore = currentState.score,
                    totalQuestions = currentState.questions.size,
                    questions = currentState.questions
                )
            }
        }
    }

    suspend fun completeDayAndSave(quizScore: Int, checklistCompleted: Boolean, incidentReported: Boolean) {
        safetyScoreRepository.completeDay(quizScore, checklistCompleted, incidentReported)
    }

    private fun getFallbackQuestions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                question = "What is the primary purpose of a hard hat?",
                options = listOf("To keep your head warm", "To protect against falling objects", "To identify your job role", "To hold a headlamp"),
                correctAnswer = "To protect against falling objects",
                explanation = "Hard hats are designed to absorb the impact of falling debris and protect the skull."
            ),
            QuizQuestion(
                question = "Before using any power tool, what should you do first?",
                options = listOf("Plug it in", "Check for any damage to the cord or casing", "Turn it on to test it", "Remove the safety guard"),
                correctAnswer = "Check for any damage to the cord or casing",
                explanation = "Always inspect tools for damage before use to prevent electrical shocks or accidents."
            ),
            QuizQuestion(
                question = "When lifting heavy objects, you should lift with your...",
                options = listOf("Back", "Arms", "Legs", "Shoulders"),
                correctAnswer = "Legs",
                explanation = "Lifting with your legs keeps your back straight and prevents spinal injuries."
            ),
            QuizQuestion(
                question = "What should you do if you notice a spilled liquid on the floor?",
                options = listOf("Ignore it", "Walk around it", "Clean it up immediately or mark it with a warning sign", "Wait for the cleaning staff"),
                correctAnswer = "Clean it up immediately or mark it with a warning sign",
                explanation = "Spills are slip hazards and must be addressed immediately."
            ),
            QuizQuestion(
                question = "Why is high-visibility clothing important on site?",
                options = listOf("It looks professional", "It keeps you cool", "It makes you easily visible to equipment operators", "It protects from chemicals"),
                correctAnswer = "It makes you easily visible to equipment operators",
                explanation = "High-vis vests ensure drivers and operators can see you, preventing struck-by accidents."
            )
        )
    }
}
