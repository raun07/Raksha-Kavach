package com.rakshakavach.ui.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakshakavach.domain.model.PPEItem
import com.rakshakavach.domain.model.RiskLevel
import com.rakshakavach.ui.SharedViewModel
import com.rakshakavach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    val selectedTask by sharedViewModel.selectedTask.collectAsState()
    val checkedItems by sharedViewModel.checkedItems.collectAsState()
    val currentRiskLevel by sharedViewModel.currentRiskLevel.collectAsState()

    val task = selectedTask
    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No task selected.", color = Color.White)
        }
        return
    }

    val missingItems = task.mandatoryPPE.filterNot { checkedItems.contains(it) }
    val isFullyEquipped = missingItems.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PPE Avatar", color = Color.White) },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("← Checklist")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onNavigateToQuiz,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowPrimary,
                            contentColor = BlackBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Go to Quiz →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            if (isFullyEquipped) {
                FullyEquippedBanner()
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }

            // Avatar Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(DarkSurface, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                WorkerAvatarComposable(
                    checkedItems = checkedItems,
                    missingItems = missingItems.toSet()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Risk Level Badge
            val riskColor = when (currentRiskLevel) {
                RiskLevel.LOW -> RiskLow
                RiskLevel.MEDIUM -> RiskMedium
                RiskLevel.HIGH -> RiskHigh
                RiskLevel.CRITICAL -> RiskCritical
            }
            
            Surface(
                color = riskColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, riskColor)
            ) {
                Text(
                    text = "Risk Level: ${currentRiskLevel.name}",
                    color = riskColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Missing Items Chips
            if (missingItems.isNotEmpty()) {
                Text(
                    text = "Missing Gear:",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(missingItems) { item ->
                        Surface(
                            color = RiskCritical.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, RiskCritical)
                        ) {
                            Text(
                                text = item.name.replace("_", " "),
                                color = RiskCritical,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullyEquippedBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "banner_alpha"
    )

    Surface(
        color = RiskLow.copy(alpha = alpha * 0.3f),
        border = BorderStroke(1.dp, RiskLow.copy(alpha = alpha)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("✅", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Fully Equipped & Ready for Work!",
                color = RiskLow,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun WorkerAvatarComposable(
    checkedItems: Set<PPEItem>,
    missingItems: Set<PPEItem>
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val helmetChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.HELMET)) 1f else 0f)
    val gogglesChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.GOGGLES)) 1f else 0f)
    val faceShieldChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.FACE_SHIELD)) 1f else 0f)
    val vestChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.VEST)) 1f else 0f)
    val apronChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.APRON)) 1f else 0f)
    val harnessChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.HARNESS)) 1f else 0f)
    val glovesChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.GLOVES)) 1f else 0f)
    val bootsChecked by animateFloatAsState(if (checkedItems.contains(PPEItem.BOOTS)) 1f else 0f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val centerX = width / 2f
        val headRadius = 35f
        val headY = height * 0.2f
        
        val torsoWidth = 70f
        val torsoHeight = 110f
        val torsoY = headY + headRadius + 10f
        
        val armWidth = 20f
        val armHeight = 80f
        val leftArmX = centerX - torsoWidth/2 - armWidth - 5f
        val rightArmX = centerX + torsoWidth/2 + 5f
        
        val legWidth = 24f
        val legHeight = 90f
        val leftLegX = centerX - legWidth - 5f
        val rightLegX = centerX + 5f
        val legY = torsoY + torsoHeight + 5f

        // 1. Draw Base Worker Body (Grey)
        val bodyColor = Color(0xFF888888)
        
        // Head
        drawCircle(color = bodyColor, radius = headRadius, center = Offset(centerX, headY))
        // Torso
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(centerX - torsoWidth/2, torsoY),
            size = Size(torsoWidth, torsoHeight),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Arms
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(leftArmX, torsoY),
            size = Size(armWidth, armHeight),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(rightArmX, torsoY),
            size = Size(armWidth, armHeight),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Legs
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(leftLegX, legY),
            size = Size(legWidth, legHeight),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(rightLegX, legY),
            size = Size(legWidth, legHeight),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Helper function to draw glowing bounds for missing items
        fun drawMissingPulse(topLeft: Offset, size: Size, isCircle: Boolean = false, radius: Float = 0f, center: Offset = Offset.Zero) {
            val strokeWidth = 4f
            val color = RiskCritical.copy(alpha = pulseAlpha)
            if (isCircle) {
                drawCircle(color = color, radius = radius, center = center, style = Stroke(strokeWidth))
            } else {
                drawRoundRect(
                    color = color,
                    topLeft = topLeft,
                    size = size,
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(strokeWidth)
                )
            }
        }

        // 2. Overlays - HELMET
        if (helmetChecked > 0f) {
            val path = Path().apply {
                moveTo(centerX - headRadius - 10f, headY)
                quadraticBezierTo(centerX, headY - headRadius - 20f, centerX + headRadius + 10f, headY)
                close()
            }
            drawPath(path = path, color = YellowPrimary.copy(alpha = helmetChecked))
            // Green glow
            drawPath(path = path, color = RiskLow.copy(alpha = helmetChecked * 0.3f), style = Stroke(6f))
        } else if (missingItems.contains(PPEItem.HELMET)) {
            val path = Path().apply {
                moveTo(centerX - headRadius - 10f, headY)
                quadraticBezierTo(centerX, headY - headRadius - 20f, centerX + headRadius + 10f, headY)
                close()
            }
            drawPath(path = path, color = RiskCritical.copy(alpha = pulseAlpha), style = Stroke(4f))
        }

        // 3. Overlays - GOGGLES / FACE SHIELD
        val faceOverlayAlpha = maxOf(gogglesChecked, faceShieldChecked)
        if (faceOverlayAlpha > 0f) {
            drawRoundRect(
                color = Color(0xFF4FC3F7).copy(alpha = faceOverlayAlpha * 0.8f),
                topLeft = Offset(centerX - 20f, headY - 5f),
                size = Size(40f, 15f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            // Green glow
            drawRoundRect(
                color = RiskLow.copy(alpha = faceOverlayAlpha * 0.3f),
                topLeft = Offset(centerX - 20f, headY - 5f),
                size = Size(40f, 15f),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(4f)
            )
        } else if (missingItems.contains(PPEItem.GOGGLES) || missingItems.contains(PPEItem.FACE_SHIELD)) {
            drawMissingPulse(Offset(centerX - 20f, headY - 5f), Size(40f, 15f))
        }

        // 4. Overlays - VEST
        if (vestChecked > 0f) {
            drawRoundRect(
                color = Color(0xFFFF9800).copy(alpha = vestChecked),
                topLeft = Offset(centerX - torsoWidth/2 - 2f, torsoY + 10f),
                size = Size(torsoWidth + 4f, torsoHeight * 0.7f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = RiskLow.copy(alpha = vestChecked * 0.3f),
                topLeft = Offset(centerX - torsoWidth/2 - 2f, torsoY + 10f),
                size = Size(torsoWidth + 4f, torsoHeight * 0.7f),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(4f)
            )
        } else if (missingItems.contains(PPEItem.VEST)) {
            drawMissingPulse(Offset(centerX - torsoWidth/2, torsoY + 10f), Size(torsoWidth, torsoHeight * 0.7f))
        }

        // 5. Overlays - APRON
        if (apronChecked > 0f) {
            drawRoundRect(
                color = Color(0xFFB0BEC5).copy(alpha = apronChecked),
                topLeft = Offset(centerX - torsoWidth/2 + 5f, torsoY + 30f),
                size = Size(torsoWidth - 10f, torsoHeight + 20f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = RiskLow.copy(alpha = apronChecked * 0.3f),
                topLeft = Offset(centerX - torsoWidth/2 + 5f, torsoY + 30f),
                size = Size(torsoWidth - 10f, torsoHeight + 20f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(4f)
            )
        } else if (missingItems.contains(PPEItem.APRON)) {
            drawMissingPulse(Offset(centerX - torsoWidth/2 + 5f, torsoY + 30f), Size(torsoWidth - 10f, torsoHeight + 20f))
        }

        // 6. Overlays - HARNESS
        if (harnessChecked > 0f) {
            drawLine(
                color = YellowPrimary.copy(alpha = harnessChecked),
                start = Offset(centerX - torsoWidth/2, torsoY),
                end = Offset(centerX + torsoWidth/2, torsoY + torsoHeight),
                strokeWidth = 8f
            )
            drawLine(
                color = YellowPrimary.copy(alpha = harnessChecked),
                start = Offset(centerX + torsoWidth/2, torsoY),
                end = Offset(centerX - torsoWidth/2, torsoY + torsoHeight),
                strokeWidth = 8f
            )
        } else if (missingItems.contains(PPEItem.HARNESS)) {
            drawLine(
                color = RiskCritical.copy(alpha = pulseAlpha),
                start = Offset(centerX - torsoWidth/2, torsoY),
                end = Offset(centerX + torsoWidth/2, torsoY + torsoHeight),
                strokeWidth = 6f
            )
            drawLine(
                color = RiskCritical.copy(alpha = pulseAlpha),
                start = Offset(centerX + torsoWidth/2, torsoY),
                end = Offset(centerX - torsoWidth/2, torsoY + torsoHeight),
                strokeWidth = 6f
            )
        }

        // 7. Overlays - GLOVES
        if (glovesChecked > 0f) {
            val gloveColor = Color(0xFF795548).copy(alpha = glovesChecked)
            drawRoundRect(
                color = gloveColor,
                topLeft = Offset(leftArmX - 2f, torsoY + armHeight - 15f),
                size = Size(armWidth + 4f, 25f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = gloveColor,
                topLeft = Offset(rightArmX - 2f, torsoY + armHeight - 15f),
                size = Size(armWidth + 4f, 25f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        } else if (missingItems.contains(PPEItem.GLOVES)) {
            drawMissingPulse(Offset(leftArmX, torsoY + armHeight - 15f), Size(armWidth, 25f))
            drawMissingPulse(Offset(rightArmX, torsoY + armHeight - 15f), Size(armWidth, 25f))
        }

        // 8. Overlays - BOOTS
        if (bootsChecked > 0f) {
            val bootColor = Color(0xFF333333).copy(alpha = bootsChecked)
            drawRoundRect(
                color = bootColor,
                topLeft = Offset(leftLegX - 4f, legY + legHeight - 20f),
                size = Size(legWidth + 8f, 25f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = bootColor,
                topLeft = Offset(rightLegX - 4f, legY + legHeight - 20f),
                size = Size(legWidth + 8f, 25f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        } else if (missingItems.contains(PPEItem.BOOTS)) {
            drawMissingPulse(Offset(leftLegX - 2f, legY + legHeight - 20f), Size(legWidth + 4f, 25f))
            drawMissingPulse(Offset(rightLegX - 2f, legY + legHeight - 20f), Size(legWidth + 4f, 25f))
        }
    }
}
