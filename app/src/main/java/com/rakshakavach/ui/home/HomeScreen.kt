package com.rakshakavach.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakshakavach.data.local.IncidentEntity
import com.rakshakavach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTaskSelector: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: SafetyScoreViewModel = hiltViewModel()
) {
    val safetyScore by viewModel.safetyScore.collectAsState()
    val recentIncidents by viewModel.recentIncidents.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛡️ ", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "RAKSHA-KAVACH",
                            color = YellowPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackBackground,
                    titleContentColor = YellowPrimary
                )
            )
        },
        containerColor = BlackBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Safety Score Hero Card
            SafetyScoreCard(
                score = safetyScore?.currentScore ?: 0,
                streak = safetyScore?.currentStreak ?: 0,
                badges = if (safetyScore?.badges.isNullOrEmpty()) 0 else safetyScore?.badges?.split(",")?.size ?: 0
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Checklist Button
            Button(
                onClick = onNavigateToTaskSelector,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowPrimary,
                    contentColor = BlackBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Start Today's Checklist",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(
                    title = "Total Check-ins",
                    value = (safetyScore?.totalDaysCheckedIn ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    title = "Best Streak",
                    value = (safetyScore?.bestStreak ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    title = "Incidents",
                    value = recentIncidents.size.toString(), // Simplified
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Incidents
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Incidents",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                TextButton(onClick = { /* Navigate to Incident History */ }) {
                    Text("View All", color = YellowPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (recentIncidents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✅", style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No incidents yet — stay safe!",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(recentIncidents.take(3)) { incident ->
                        IncidentCard(incident)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Tip
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkSurface, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = BlackBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(YellowPrimary)
                    )
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Daily Safety Tip",
                            color = YellowPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dailyTip,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyScoreCard(score: Int, streak: Int, badges: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = YellowPrimary.copy(alpha = alpha))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = BlackBackground
            )
            Text(
                text = "Safety Score",
                style = MaterialTheme.typography.titleMedium,
                color = BlackBackground.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BlackBackground.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "🔥 $streak Day Streak",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = BlackBackground,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = BlackBackground.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "⭐ $badges Badges",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = BlackBackground,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(YellowPrimary)
            )
        }
    }
}

@Composable
fun IncidentCard(incident: IncidentEntity) {
    val severityColor = when(incident.severity) {
        "NEAR_MISS" -> RiskMedium
        "MINOR" -> RiskHigh
        "SERIOUS" -> RiskCritical
        else -> MidGrey
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(severityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = incident.taskName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = incident.date,
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = incident.description,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}
