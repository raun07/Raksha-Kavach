package com.rakshakavach.ui.incidentlog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakshakavach.data.local.IncidentEntity
import com.rakshakavach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: IncidentViewModel = hiltViewModel()
) {
    val incidents by viewModel.incidents.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filters = listOf("All", "NEAR_MISS", "MINOR", "SERIOUS")
    val filteredIncidents = if (selectedFilter == "All") incidents else incidents.filter { it.severity == selectedFilter }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incident History", color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                containerColor = BlackBackground,
                contentColor = YellowPrimary,
                edgePadding = 16.dp,
                divider = {}
            ) {
                filters.forEach { filter ->
                    val title = when (filter) {
                        "NEAR_MISS" -> "Near Miss"
                        "All" -> "All"
                        else -> filter.lowercase().replaceFirstChar { it.uppercase() }
                    }
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = { 
                            Text(
                                text = title,
                                color = if (selectedFilter == filter) YellowPrimary else MidGrey
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredIncidents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👷‍♂️", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No incidents recorded",
                            color = MidGrey,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredIncidents) { incident ->
                        IncidentHistoryCard(incident)
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentHistoryCard(incident: IncidentEntity) {
    var expanded by remember { mutableStateOf(false) }
    
    val severityColor = when (incident.severity) {
        "NEAR_MISS" -> RiskMedium
        "MINOR" -> RiskHigh
        "SERIOUS" -> RiskCritical
        else -> MidGrey
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(severityColor)
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = incident.taskName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        color = severityColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = incident.severity.replace("_", " "),
                            color = severityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = incident.date,
                    color = MidGrey,
                    style = MaterialTheme.typography.labelSmall
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = incident.description,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                )
            }
        }
    }
}
