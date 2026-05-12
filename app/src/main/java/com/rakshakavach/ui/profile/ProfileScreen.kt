package com.rakshakavach.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rakshakavach.ui.home.SafetyScoreViewModel
import com.rakshakavach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToIncidentHistory: () -> Unit,
    viewModel: SafetyScoreViewModel = hiltViewModel()
) {
    val safetyScore by viewModel.safetyScore.collectAsState()
    val recentIncidents by viewModel.recentIncidents.collectAsState()
    
    val score = safetyScore?.currentScore ?: 0
    val streak = safetyScore?.currentStreak ?: 0
    val bestStreak = safetyScore?.bestStreak ?: 0
    val checkins = safetyScore?.totalDaysCheckedIn ?: 0
    val badgesStr = safetyScore?.badges ?: ""
    val userBadges = badgesStr.split(",").filter { it.isNotBlank() }

    val allAvailableBadges = listOf(
        "Safe Starter" to "🌱",
        "Safety Champion" to "🏆",
        "Safety Legend" to "👑"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(YellowPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("👷‍♂️", fontSize = 50.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Worker",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Safety Score:", color = MidGrey, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$score",
                        color = YellowPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStatCard(title = "Current Streak", value = "$streak", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                ProfileStatCard(title = "Best Streak", value = "$bestStreak", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStatCard(title = "Total Check-ins", value = "$checkins", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                ProfileStatCard(title = "Incidents", value = "${recentIncidents.size}", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text("Badges & Achievements", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(allAvailableBadges.size) { index ->
                    val (badgeName, icon) = allAvailableBadges[index]
                    val isEarned = userBadges.contains(badgeName)
                    
                    Card(
                        modifier = Modifier.width(140.dp).height(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEarned) DarkSurface else BlackBackground
                        ),
                        border = BorderStroke(1.dp, if (isEarned) YellowPrimary else MidGrey),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = if (isEarned) icon else "🔒", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = badgeName,
                                color = if (isEarned) Color.White else MidGrey,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            if (isEarned) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Earned",
                                    color = YellowPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToIncidentHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = YellowPrimary),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, YellowPrimary)
            ) {
                Text("View Full History", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, color = MidGrey, style = MaterialTheme.typography.labelSmall)
        }
    }
}
