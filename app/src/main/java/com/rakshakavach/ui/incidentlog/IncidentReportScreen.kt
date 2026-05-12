package com.rakshakavach.ui.incidentlog

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakshakavach.data.local.IncidentEntity
import com.rakshakavach.ui.SharedViewModel
import com.rakshakavach.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportScreen(
    onNavigateBack: () -> Unit,
    sharedViewModel: SharedViewModel,
    viewModel: IncidentViewModel = hiltViewModel()
) {
    val submitState by viewModel.submitState.collectAsState()
    val selectedTask by sharedViewModel.selectedTask.collectAsState()
    
    var description by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf("NEAR_MISS") }
    
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val currentDateStr = dateFormatter.format(Date())

    LaunchedEffect(submitState) {
        if (submitState is IncidentSubmitState.Success) {
            sharedViewModel.flagIncidentToday()
            delay(1500)
            viewModel.resetSubmitState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Warning", tint = RiskMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Report Incident", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (submitState is IncidentSubmitState.Success) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedCheckmark()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Report Submitted Successfully", color = RiskLow, style = MaterialTheme.typography.titleLarge)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = selectedTask?.taskName?.english ?: "General Work",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Task") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = YellowPrimary,
                            unfocusedBorderColor = MidGrey
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Today, $currentDateStr", color = MidGrey, style = MaterialTheme.typography.labelSmall)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Severity", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SeverityChip(
                            label = "Near Miss",
                            color = RiskMedium,
                            isSelected = selectedSeverity == "NEAR_MISS",
                            onClick = { selectedSeverity = "NEAR_MISS" },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SeverityChip(
                            label = "Minor",
                            color = RiskHigh,
                            isSelected = selectedSeverity == "MINOR",
                            onClick = { selectedSeverity = "MINOR" },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SeverityChip(
                            label = "Serious",
                            color = RiskCritical,
                            isSelected = selectedSeverity == "SERIOUS",
                            onClick = { selectedSeverity = "SERIOUS" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Describe what happened...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        minLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = YellowPrimary,
                            unfocusedBorderColor = MidGrey
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val entity = IncidentEntity(
                                taskId = selectedTask?.taskId ?: "GEN",
                                taskName = selectedTask?.taskName?.english ?: "General Work",
                                description = description.ifBlank { "No description provided." },
                                severity = selectedSeverity,
                                timestamp = System.currentTimeMillis(),
                                date = currentDateStr.split(" ")[0]
                            )
                            viewModel.submitIncident(entity)
                        },
                        enabled = submitState !is IncidentSubmitState.Submitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = BlackBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (submitState is IncidentSubmitState.Submitting) {
                            CircularProgressIndicator(color = BlackBackground, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Submit Report", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeverityChip(label: String, color: Color, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        color = if (isSelected) color else Color.Transparent,
        border = BorderStroke(1.dp, color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) BlackBackground else color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun AnimatedCheckmark() {
    var drawTo by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(500)
        ) { value, _ -> drawTo = value }
    }

    Canvas(modifier = Modifier.size(100.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.75f)
            lineTo(size.width * 0.8f, size.height * 0.25f)
        }
        
        val pathMeasure = androidx.compose.ui.graphics.PathMeasure()
        pathMeasure.setPath(path, false)
        
        val drawPath = Path()
        pathMeasure.getSegment(0f, pathMeasure.length * drawTo, drawPath, true)
        
        drawPath(
            path = drawPath,
            color = RiskLow,
            style = Stroke(width = 10f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
    }
}
