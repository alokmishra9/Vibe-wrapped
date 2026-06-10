package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoodSpectrumPlot(
    energy: Float,
    danceability: Float,
    valence: Float,
    acousticness: Float,
    modifier: Modifier = Modifier
) {
    var animateStart by remember { mutableStateOf(false) }
    
    val animatedEnergy by animateFloatAsState(
        targetValue = if (animateStart) energy else 0f,
        animationSpec = tween(1000)
    )
    val animatedDance by animateFloatAsState(
        targetValue = if (animateStart) danceability else 0f,
        animationSpec = tween(1100)
    )
    val animatedValence by animateFloatAsState(
        targetValue = if (animateStart) valence else 0f,
        animationSpec = tween(1200)
    )
    val animatedAcoustic by animateFloatAsState(
        targetValue = if (animateStart) acousticness else 0f,
        animationSpec = tween(1300)
    )

    LaunchedEffect(Unit) {
        animateStart = true
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SpectrumBar(
            label = "Vibe Energy (Vibrancy)",
            value = animatedEnergy,
            colorStart = Color(0xFF00FFCC),
            colorEnd = Color(0xFF00FF66),
            leftDesc = "Chill / Subdued",
            rightDesc = "High Voltage"
        )
        
        SpectrumBar(
            label = "Groove Rate (Danceability)",
            value = animatedDance,
            colorStart = Color(0xFFFF007A),
            colorEnd = Color(0xFFFF5E00),
            leftDesc = "Stiff / Complex",
            rightDesc = "Absolute Banger"
        )

        SpectrumBar(
            label = "Valence Hue (Positivity)",
            value = animatedValence,
            colorStart = Color(0xFFEEEF20),
            colorEnd = Color(0xFF00F0FF),
            leftDesc = "Melancholic Blue",
            rightDesc = "Euphoric Gold"
        )

        SpectrumBar(
            label = "Organic Depth (Acousticness)",
            value = animatedAcoustic,
            colorStart = Color(0xFFA855F7),
            colorEnd = Color(0xFFE47CFF),
            leftDesc = "Synthesized / Cyber",
            rightDesc = "Acoustic / Pure"
        )
    }
}

@Composable
private fun SpectrumBar(
    label: String,
    value: Float,
    colorStart: Color,
    colorEnd: Color,
    leftDesc: String,
    rightDesc: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = colorStart,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Custom neon slider bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0x22FFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value.coerceIn(0.01f, 1f))
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(colorStart, colorEnd)
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftDesc,
                color = Color.Gray,
                fontSize = 10.sp
            )
            Text(
                text = rightDesc,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}
