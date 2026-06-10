package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GenreShare
import com.example.data.ListeningArchetype
import com.example.data.SavedWrapped
import com.example.ui.components.GenreDnaChart
import com.example.ui.components.MoodSpectrumPlot
import com.example.ui.components.ShareCardExporter
import com.example.ui.viewmodel.WrappedViewModel

@Composable
fun StorySlidesScreen(
    viewModel: WrappedViewModel,
    wrap: SavedWrapped,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Parse the genre percentages saved inside the database
    val genresList = remember(wrap.genresJson) {
        try {
            val colorPalette = listOf("#FF007A", "#00F0FF", "#EEEF20", "#1DB954", "#A855F7")
            wrap.genresJson.split(",").mapIndexed { idx, pair ->
                val tokens = pair.split(":")
                val pct = tokens.getOrNull(1)?.toIntOrNull() ?: 20
                GenreShare(
                    name = tokens.getOrNull(0) ?: "Indie",
                    percentage = pct,
                    hexColor = colorPalette.getOrElse(idx % colorPalette.size) { "#00FFCC" }
                )
            }
        } catch (e: Exception) {
            listOf(GenreShare("Alternative", 100, "#1DB954"))
        }
    }

    // Determine current slide colors for dynamic story background gradients
    val slideColors = when (currentIndex) {
        0 -> listOf(Color(0xFF0D0E15), Color(0xFF131525)) // Dark sci-fi Scan
        1 -> listOf(Color(0xFF180315), Color(0xFF0F020E)) // Extreme Dark Fuchsia (Genres)
        2 -> listOf(Color(0xFF02131C), Color(0xFF010A10)) // Extreme Dark Cyber Blue (Moods)
        3 -> listOf(Color(0xFF02140A), Color(0xFF010A05)) // Extreme Dark Mint Green (Archetype)
        else -> listOf(Color(0xFF08090C), Color(0xFF151825)) // Classic Slate (Alter Ego / Share card)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                // Spotify-Wrapped overhead segment indicators
                Row(
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0..4) {
                        val isFilledValue = i <= currentIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isFilledValue) Color.White else Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Short exit controller
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIBE STORY ${currentIndex + 1}/5",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = { viewModel.navigateToWelcome() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit slideshow",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .drawBehind {
                    val primaryGlowColor = when (currentIndex) {
                        0 -> Color(0x3B1DB954) // Mint green
                        1 -> Color(0x3BFF007A) // Fuchsia
                        2 -> Color(0x3B00F0FF) // Cyber Cyan
                        3 -> Color(0x3B10B981) // Emerald
                        else -> Color(0x3BBD00FF) // Neon Purple / Alter ego
                    }
                    val secondaryGlowColor = when (currentIndex) {
                        0 -> Color(0x2E00F0FF) // Cyan
                        1 -> Color(0x2EBD00FF) // Purple
                        2 -> Color(0x2E1D4ED8) // Deep Blue
                        3 -> Color(0x2E00F0FF) // Cyber Cyan
                        else -> Color(0x2E1DB954) // Green
                    }

                    // Top-Left radial glow circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryGlowColor, Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f),
                            radius = size.minDimension * 0.75f
                        )
                    )
                    // Bottom-Right radial glow circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryGlowColor, Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.8f),
                            radius = size.minDimension * 0.75f
                        )
                    )
                }
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Content Switcher based on Slide index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (currentIndex) {
                        0 -> ScanIntroSlide(wrap.profileName)
                        1 -> GenreDnaSlide(genresList)
                        2 -> MoodSpectrumSlide(wrap)
                        3 -> ListeningArchetypeSlide(wrap.archetype)
                        4 -> FinalSummaryShareCardSlide(wrap, genresList) {
                            ShareCardExporter.exportAndShare(context, wrap, genresList)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Control Action Row (Previous/Next)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.prevSlide(wrap, currentIndex) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PREV", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { viewModel.nextSlide(wrap, currentIndex) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (currentIndex) {
                                1 -> Color(0xFFFF007A)
                                2 -> Color(0xFF00F0FF)
                                3 -> Color(0xFF1DB954)
                                else -> Color.White
                            },
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(
                            text = if (currentIndex < 4) "NEXT" else "DONE",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward"
                        )
                    }
                }
            }
        }
    }
}

// --- Specific slide sub-composables ---

@Composable
fun ScanIntroSlide(profileName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SYNTHESIS LOCKED ⚙️",
            color = Color(0xFF1DB954),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome, @$profileName",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "We have crawled, sliced, and fully mapped your audio habits. Let's peel back the layers of your musical soul.",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun GenreDnaSlide(genresList: List<GenreShare>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "GENRE DNA",
            color = Color(0xFFFF007A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Your Sound Mosaic",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        GenreDnaChart(
            shares = genresList,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MoodSpectrumSlide(wrap: SavedWrapped) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "MOOD SPECTRUM",
            color = Color(0xFF00F0FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Audio Feature Blueprint",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))

        MoodSpectrumPlot(
            energy = wrap.energy,
            danceability = wrap.danceability,
            valence = wrap.valence,
            acousticness = wrap.acousticness
        )
    }
}

@Composable
fun ListeningArchetypeSlide(archetypeName: String) {
    val archetype = remember(archetypeName) {
        ListeningArchetype.ALL.firstOrNull { it.name == archetypeName } ?: ListeningArchetype.ALL.first()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "YOUR PROFILE MATCH",
            color = Color(0xFF1DB954),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Large high-impact holographic badge card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x14FFFFFF))
                .border(2.dp, Color(0xFFBD00FF), RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = archetype.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = archetype.tagline,
                    fontSize = 15.sp,
                    color = Color(0xFF1DB954),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = archetype.description,
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Acoustic Archetype Match:\n${archetype.moodMatch}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FinalSummaryShareCardSlide(
    wrap: SavedWrapped,
    genresList: List<GenreShare>,
    onShareClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "THE REVEAL",
            color = Color(0xFFEEEF20),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Shareable Music Card",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Cyber Trade Card Simulation Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x14FFFFFF))
                .border(2.dp, Color(0xFFBD00FF), RoundedCornerShape(32.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALTER EGO PRO",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "@${wrap.profileName.uppercase()}",
                        color = Color(0xFFFF007A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = wrap.alterEgoName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 28.sp
                )
                Text(
                    text = wrap.alterEgoDescription,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "THE DIAGNOSIS ROAST:",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wrap.roast,
                    fontSize = 13.sp,
                    color = Color(0xFF00FFCC),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // High-virality Share Button
        Button(
            onClick = onShareClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share Card")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SHARE PNG TO INSTAGRAM/TIKTOK",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
