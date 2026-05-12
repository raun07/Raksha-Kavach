package com.rakshakavach.ui.checklist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rakshakavach.domain.model.PPEItem
import com.rakshakavach.domain.model.RiskLevel
import com.rakshakavach.ui.SharedViewModel
import com.rakshakavach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToAvatar: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    val selectedTask by sharedViewModel.selectedTask.collectAsState()
    val checkedItems by sharedViewModel.checkedItems.collectAsState()
    val currentRiskLevel by sharedViewModel.currentRiskLevel.collectAsState()
    val currentRiskScore by sharedViewModel.currentRiskScore.collectAsState()
    val likelyInjuries by sharedViewModel.likelyInjuries.collectAsState()
    
    val haptic = LocalHapticFeedback.current

    val task = selectedTask
    if (task == null) {
        // Fallback UI if state is lost
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No task selected.", color = Color.White)
        }
        return
    }

    val totalItems = task.mandatoryPPE.size
    val checkedCount = checkedItems.size
    val progress = if (totalItems > 0) checkedCount.toFloat() / totalItems else 0f
    
    var showCelebration by remember { mutableStateOf(false) }
    
    LaunchedEffect(checkedCount) {
        if (totalItems > 0 && checkedCount == totalItems) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showCelebration = true
            kotlinx.coroutines.delay(1500)
            showCelebration = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Checklist", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAvatar,
                containerColor = YellowPrimary,
                contentColor = BlackBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("View Avatar 🧑‍🏭", fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNavigateToQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowPrimary,
                        contentColor = BlackBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Proceed to Quiz →",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TOP SECTION: Task Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = getTaskEmoji(task.taskName.english),
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = task.taskName.english,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = task.taskName.hindi,
                                color = MidGrey,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$checkedCount of $totalItems items checked",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                        color = YellowPrimary,
                        trackColor = BlackBackground,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                }
            }

            // MIDDLE SECTION: PPE Checklist
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(task.mandatoryPPE) { ppe ->
                    val isChecked = checkedItems.contains(ppe)
                    PPEChecklistItem(
                        item = ppe,
                        riskWeight = task.riskWeight[ppe] ?: 0,
                        isChecked = isChecked,
                        onToggle = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            sharedViewModel.togglePPEItem(ppe) 
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // BOTTOM SECTION: Risk Meter Card (Sticky)
            RiskMeterCard(
                riskLevel = currentRiskLevel,
                riskScore = currentRiskScore,
                likelyInjuries = likelyInjuries
            )
        }
        
        // Fully Safe Celebration Overlay
        androidx.compose.animation.AnimatedVisibility(
            visible = showCelebration,
            enter = androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300)),
            exit = androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RiskLow.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛡️", fontSize = 80.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "✓ You're Fully Protected!", 
                        color = Color.White, 
                        fontSize = 28.sp, 
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PPEChecklistItem(
    item: PPEItem,
    riskWeight: Int,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked) DarkSurface else Color(0x22D32F2F),
        label = "ppe_bg_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isChecked) 1f else 1.02f,
        label = "ppe_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isChecked) Color.Transparent else RiskCritical.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = getPPEEmoji(item), fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.replace("_", " "),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Risk Impact: $riskWeight",
                    color = MidGrey,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            // Toggle Button
            if (isChecked) {
                Button(
                    onClick = onToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RiskLow,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("✓ Checked", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = onToggle,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RiskCritical
                    ),
                    border = BorderStroke(2.dp, RiskCritical),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("✗ Missing", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RiskMeterCard(
    riskLevel: RiskLevel,
    riskScore: Int,
    likelyInjuries: List<String>
) {
    val activeColor = when (riskLevel) {
        RiskLevel.LOW -> RiskLow
        RiskLevel.MEDIUM -> RiskMedium
        RiskLevel.HIGH -> RiskHigh
        RiskLevel.CRITICAL -> RiskCritical
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, activeColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Risk Level",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${minOf(riskScore, 100)}%",
                    color = activeColor,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Bar
            Row(modifier = Modifier.fillMaxWidth()) {
                val segments = listOf(
                    RiskLevel.LOW to RiskLow,
                    RiskLevel.MEDIUM to RiskMedium,
                    RiskLevel.HIGH to RiskHigh,
                    RiskLevel.CRITICAL to RiskCritical
                )

                segments.forEachIndexed { index, (level, color) ->
                    val isActive = riskLevel == level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .padding(horizontal = 2.dp)
                            .background(
                                color = if (isActive) color else color.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                listOf("LOW", "MED", "HIGH", "CRIT").forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MidGrey,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (riskLevel != RiskLevel.LOW && likelyInjuries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Likely Injuries:",
                    color = RiskCritical,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    likelyInjuries.forEach { injury ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(RiskCritical.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚠", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = injury,
                                color = RiskCritical,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getTaskEmoji(taskName: String): String {
    return when(taskName) {
        "Welding" -> "🔥"
        "Height Work" -> "🏗️"
        "Trench Digging" -> "⛏️"
        "Electrical Work" -> "⚡"
        "Heavy Machinery" -> "🚜"
        "Chemical Handling" -> "🧪"
        "Painting" -> "🎨"
        "Demolition" -> "🏚️"
        "Carpentry" -> "🪵"
        "Loading/Unloading" -> "📦"
        else -> "👷"
    }
}

fun getPPEEmoji(item: PPEItem): String {
    return when(item) {
        PPEItem.HELMET -> "👷"
        PPEItem.GLOVES -> "🧤"
        PPEItem.BOOTS -> "🥾"
        PPEItem.GOGGLES -> "🥽"
        PPEItem.HARNESS -> "🦺"
        PPEItem.APRON -> "🥼"
        PPEItem.RESPIRATOR -> "😷"
        PPEItem.EARPLUGS -> "🎧"
        PPEItem.VEST -> "🦺"
        PPEItem.FACE_SHIELD -> "🛡️"
    }
}
