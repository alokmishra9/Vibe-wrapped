package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.WrappedViewModel

import com.example.ui.components.ImmersiveBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    viewModel: WrappedViewModel,
    modifier: Modifier = Modifier
) {
    var profileName by remember { mutableStateOf("") }
    var favArtists by remember { mutableStateOf("") }
    var favGenres by remember { mutableStateOf("") }
    var selectedVibeScale by remember { mutableStateOf("Sunset") }
    var showDeveloperMode by remember { mutableStateOf(false) }
    var customClientId by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val optionsVibe = listOf("Chill", "Banger", "Moody", "Sunset", "Goth")

    ImmersiveBg(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo Header
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Music Icon",
                modifier = Modifier.size(56.dp),
                tint = Color(0xFF1DB954)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "VIBE WRAPPED",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Discover your listening DNA & music alter-ego.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Core Integrations Card (Spotify OAuth)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Real-Time Spotify OAuth",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sign in directly to analyze your biological Spotify listening log dynamically. Completely safe, local, and instant.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.launchSpotifyLogin(customClientId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "CONNECT SPOTIFY ACCOUNT",
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Optional Custom Client ID config dropdown
                    Text(
                        text = if (showDeveloperMode) "Hide Custom Secrets" else "Use Custom Spotify Client ID",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { showDeveloperMode = !showDeveloperMode }
                            .padding(4.dp)
                    )

                    if (showDeveloperMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customClientId,
                            onValueChange = { customClientId = it },
                            label = { Text("Client ID", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF1DB954),
                                unfocusedBorderColor = Color.Gray
                            ),
                            placeholder = { Text("e.g. b48a...", color = Color.DarkGray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Ensure you add the callback 'https://localhost/callback' in your Spotify developer dashboard settings.",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Personal Vibe Builder Form
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Instant Vibe Analyzer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No Spotify developer account? No problem! Type in your target style, and analyze your bespoke personality DNA instantly.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = profileName,
                        onValueChange = { profileName = it },
                        label = { Text("Profile Nickname") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF007A),
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true,
                        placeholder = { Text("e.g. SynthLord99") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = favArtists,
                        onValueChange = { favArtists = it },
                        label = { Text("Favorite Artists (comma separated)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF007A),
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true,
                        placeholder = { Text("e.g. Billie Eilish, Fred again.., Lorn") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = favGenres,
                        onValueChange = { favGenres = it },
                        label = { Text("Favorite Genres (comma separated)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFF007A),
                            unfocusedBorderColor = Color.Gray
                        ),
                        singleLine = true,
                        placeholder = { Text("e.g. Techno, Dark Ambient, Indie") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Soundscape Aesthetic Tone:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        optionsVibe.forEach { tone ->
                            val isSelected = tone == selectedVibeScale
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFFF007A) else Color(0x11FFFFFF))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Transparent else Color.Gray,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedVibeScale = tone }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tone,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.loadCustomVibe(profileName, favArtists, favGenres, selectedVibeScale) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "LAUNCH DNA GENERATOR",
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Playable Simulated Preset Profiles (Horizontal badges)
            Text(
                text = "TRY COHESIVE PRE-MADE SPECIMENS:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            val presetsList = listOf(
                Triple("techno", "🔌 Industrial Techno Rave", Color(0xFF00FFCC)),
                Triple("lofi", "☕ Quiet Office Lofi Study", Color(0xFFEEEF20)),
                Triple("metal", "🎸 Progressive Metal Alchemist", Color(0xFFE47CFF)),
                Triple("classical", "🎻 Symphony Chamber Room", Color(0xFF00F0FF))
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(presetsList) { (type, label, color) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x14FFFFFF))
                            .border(1.dp, Color(0x2BFFFFFF), RoundedCornerShape(16.dp))
                            .clickable { viewModel.loadPresetDemoProfile(type) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Specimen",
                                tint = color,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Historical wraps tab trigger
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
                border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateToPastWraps() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History Icon",
                        tint = Color.Cyan
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Historical Wrapped Library",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "View, re-read, or export your saved taste visualizers.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}
