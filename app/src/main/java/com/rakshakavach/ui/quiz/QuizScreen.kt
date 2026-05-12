package com.rakshakavach.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakshakavach.ui.SharedViewModel
import com.rakshakavach.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    sharedViewModel: SharedViewModel,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTask by sharedViewModel.selectedTask.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedTask) {
        val taskName = selectedTask?.taskName?.english ?: "General Work"
        viewModel.loadQuiz(taskName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Safety Quiz", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BlackBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> LoadingState(state.taskName)
                is QuizUiState.QuizReady -> QuizReadyState(
                    state = state,
                    onOptionSelected = { viewModel.answerQuestion(it) },
                    onNextClicked = { viewModel.nextQuestion() }
                )
                is QuizUiState.QuizComplete -> QuizCompleteState(
                    state = state,
                    onComplete = { score ->
                        scope.launch {
                            sharedViewModel.setQuizResult(score)
                            val incidentFlag = sharedViewModel.incidentReportedToday.value
                            viewModel.completeDayAndSave(
                                quizScore = score,
                                checklistCompleted = true,
                                incidentReported = incidentFlag
                            )
                            snackbarHostState.showSnackbar("Safety Check Completed! Points added.")
                            onNavigateToHome()
                        }
                    }
                )
                is QuizUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = RiskCritical)
                }
            }
        }
    }
}

@Composable
fun LoadingState(taskName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = YellowPrimary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Generating your safety quiz for",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = taskName,
            color = YellowPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Powered by Gemini AI",
            color = MidGrey,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun QuizReadyState(
    state: QuizUiState.QuizReady,
    onOptionSelected: (String) -> Unit,
    onNextClicked: () -> Unit
) {
    val question = state.questions[state.currentIndex]
    val total = state.questions.size
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.currentIndex + 1} of $total",
                color = MidGrey,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Score: ${state.score}/${state.currentIndex + if(state.selectedOption != null) 1 else 0}",
                color = YellowPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1).toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = YellowPrimary,
            trackColor = DarkSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = question.question,
                modifier = Modifier.padding(24.dp),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                lineHeight = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        question.options.forEach { option ->
            val isSelected = state.selectedOption == option
            val isCorrectAnswer = option == question.correctAnswer
            
            val containerColor = when {
                state.selectedOption == null -> DarkSurface
                isCorrectAnswer -> RiskLow
                isSelected && !isCorrectAnswer -> RiskCritical
                else -> DarkSurface.copy(alpha = 0.5f)
            }
            
            val borderColor = when {
                state.selectedOption == null -> MidGrey
                isCorrectAnswer -> RiskLow
                isSelected && !isCorrectAnswer -> RiskCritical
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(12.dp),
                onClick = { 
                    if (state.selectedOption == null) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onOptionSelected(option)
                    } 
                }
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        AnimatedVisibility(
            visible = state.selectedOption != null,
            enter = fadeIn(tween(300))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Surface(
                    color = if (state.selectedOption == question.correctAnswer) RiskLow.copy(alpha = 0.15f) else RiskCritical.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = question.explanation,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onNextClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = BlackBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (state.currentIndex + 1 == total) "Finish Quiz" else "Next Question →",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun QuizCompleteState(
    state: QuizUiState.QuizComplete,
    onComplete: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            modifier = Modifier.size(150.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = DarkSurface,
            border = BorderStroke(4.dp, YellowPrimary)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${state.finalScore}/${state.totalQuestions}",
                    color = YellowPrimary,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val points = 10 + (state.finalScore * 2)
        if (state.finalScore >= 4) {
            Text("Excellent!", color = RiskLow, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("+$points points added to your Safety Score", color = Color.White, modifier = Modifier.padding(top = 8.dp))
        } else {
            Text("Keep learning!", color = RiskMedium, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("+$points points added", color = Color.White, modifier = Modifier.padding(top = 8.dp))
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { onComplete(state.finalScore) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = BlackBackground),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Complete Today's Check ✓",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
