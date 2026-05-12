package com.rakshakavach.ui.taskselector

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rakshakavach.domain.model.TaskDataSource
import com.rakshakavach.domain.model.TaskModel
import com.rakshakavach.ui.SharedViewModel
import com.rakshakavach.ui.theme.BlackBackground
import com.rakshakavach.ui.theme.DarkSurface
import com.rakshakavach.ui.theme.MidGrey
import com.rakshakavach.ui.theme.YellowPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSelectorScreen(
    onNavigateBack: () -> Unit,
    onProceedToChecklist: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    // Local state for selected task to allow the UI to reflect changes before hitting Proceed
    var localSelectedTask by remember { mutableStateOf<TaskModel?>(null) }
    
    // We get the initial task from sharedViewModel if returning to this screen
    val initialTask by sharedViewModel.selectedTask.collectAsState()
    LaunchedEffect(initialTask) {
        if (localSelectedTask == null) {
            localSelectedTask = initialTask
        }
    }

    val tasks = TaskDataSource.tasks.filter {
        it.taskName.english.contains(searchQuery, ignoreCase = true) ||
        it.taskName.hindi.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Today's Task", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground,
        bottomBar = {
            Surface(
                color = DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = localSelectedTask?.let { "Selected: ${it.taskName.english}" } ?: "No task selected",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = {
                            localSelectedTask?.let {
                                sharedViewModel.selectTask(it)
                                onProceedToChecklist()
                            }
                        },
                        enabled = localSelectedTask != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowPrimary,
                            contentColor = BlackBackground,
                            disabledContainerColor = MidGrey,
                            disabledContentColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Proceed to Checklist →",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search tasks...", color = MidGrey) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MidGrey) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YellowPrimary,
                    unfocusedBorderColor = MidGrey,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = YellowPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        isSelected = localSelectedTask?.taskId == task.taskId,
                        onClick = { localSelectedTask = task }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) YellowPrimary else Color.Transparent,
        label = "border_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )

    val iconEmoji = when(task.taskName.english) {
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        border = BorderStroke(if (isSelected) 2.dp else 0.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = YellowPrimary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = task.taskName.english,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.taskName.hindi,
                    color = MidGrey,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${task.mandatoryPPE.size} PPE Required",
                    color = YellowPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
