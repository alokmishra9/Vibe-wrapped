package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SavedWrapped
import com.example.ui.viewmodel.WrappedViewModel
import com.example.ui.components.ImmersiveBg
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWrappersLibraryScreen(
    viewModel: WrappedViewModel,
    savedWraps: List<SavedWrapped>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "HISTORICAL RUNS",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateToWelcome() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    if (savedWraps.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllWraps() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All",
                                tint = Color.Red
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        ImmersiveBg(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (savedWraps.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Empty",
                        modifier = Modifier.size(72.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Archive is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You haven't generated any music Wrapped profiles yet. Go back to create your first personal music DNA portrait!",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedWraps, key = { it.id }) { wrap ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0x2BFFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.launchSavedWrapInSlideshow(wrap) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "@${wrap.profileName}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = wrap.archetype.uppercase(),
                                        color = Color(0xFFBD00FF),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val dateStr = DateFormat.format("MMM dd, yyyy - hh:mm a", Date(wrap.timestamp))
                                    Text(
                                        text = dateStr.toString(),
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                                
                                Row {
                                    IconButton(onClick = { viewModel.launchSavedWrapInSlideshow(wrap) }) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Replay Showcase",
                                            tint = Color.Green
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteWrap(wrap.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
