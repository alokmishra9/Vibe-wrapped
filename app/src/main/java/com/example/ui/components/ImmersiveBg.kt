package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ImmersiveBg(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // Overlay ambient radiant neon glows (representing the atmospheric gradient-bg in the HTML theme)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Top-Left Neon Purple Glow (#BD00FF at approx 20%, 20%)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x3BBD00FF), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f),
                            radius = size.minDimension * 0.65f
                        )
                    )
                    // Bottom-Right Neon Cyan Glow (#00F0FF at approx 80%, 80%)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2E00F0FF), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.8f),
                            radius = size.minDimension * 0.65f
                        )
                    )
                }
        )

        content()
    }
}
