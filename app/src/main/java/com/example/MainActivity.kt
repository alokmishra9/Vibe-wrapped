package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SpotifyWebView
import com.example.ui.screens.SavedWrappersLibraryScreen
import com.example.ui.screens.StorySlidesScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WrappedUiState
import com.example.ui.viewmodel.WrappedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: WrappedViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                val isWebViewOpen by viewModel.isWebViewOpen.collectAsState()
                val spotifyAuthUrl by viewModel.spotifyAuthUrl.collectAsState()
                val savedWraps by viewModel.savedWraps.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Main State Machine Switcher
                        when (val state = uiState) {
                            is WrappedUiState.Welcome -> {
                                WelcomeScreen(viewModel = viewModel)
                            }
                            is WrappedUiState.Loading -> {
                                LoadingScreen(message = state.message)
                            }
                            is WrappedUiState.StorySlides -> {
                                StorySlidesScreen(
                                    viewModel = viewModel,
                                    wrap = state.wrap,
                                    currentIndex = state.currentSlideIndex
                                )
                            }
                            is WrappedUiState.SavedWrappersList -> {
                                SavedWrappersLibraryScreen(
                                    viewModel = viewModel,
                                    savedWraps = savedWraps
                                )
                            }
                        }

                        // Web-view Dialog Overlay
                        if (isWebViewOpen && spotifyAuthUrl.isNotEmpty()) {
                            SpotifyWebView(
                                authUrl = spotifyAuthUrl,
                                onTokenCaptured = { token ->
                                    viewModel.handleSpotifyAccessToken(token)
                                },
                                onDismiss = {
                                    viewModel.closeSpotifyWebView()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x3BBD00FF), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f),
                        radius = size.minDimension * 0.75f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2E00F0FF), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.8f),
                        radius = size.minDimension * 0.75f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1DB954),
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "DNA CALCULATION IN PROGRESS...",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1DB954),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
