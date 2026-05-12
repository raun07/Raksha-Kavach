package com.rakshakavach.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp
import com.rakshakavach.ui.theme.YellowPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(400) // Total 1.2s delay
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YellowPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🛡️",
            fontSize = 120.sp,
            modifier = Modifier.scale(scale.value)
        )
    }
}
