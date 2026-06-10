package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SpotifyClient

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SpotifyWebView(
    authUrl: String,
    onTokenCaptured: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D0E12)),
            color = Color(0xFF0D0E12)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top header controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Spotify Secure OAuth",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close WebView",
                            tint = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Render WebView securely in Jetpack Compose
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.useWideViewPort = true
                                settings.loadWithOverviewMode = true

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(
                                        view: WebView?,
                                        url: String?,
                                        favicon: Bitmap?
                                    ) {
                                        super.onPageStarted(view, url, favicon)
                                        url?.let { checkUrlForToken(it, onTokenCaptured) }
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url?.toString()
                                        if (url != null && url.startsWith(SpotifyClient.REDIRECT_URI)) {
                                            checkUrlForToken(url, onTokenCaptured)
                                            return true
                                        }
                                        return false
                                    }
                                }
                                loadUrl(authUrl)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun checkUrlForToken(url: String, onTokenCaptured: (String) -> Unit) {
    if (url.startsWith(SpotifyClient.REDIRECT_URI)) {
        // Extract token from fragment anchor part (#access_token=...)
        val fragment = url.substringAfter("#", "")
        if (fragment.isNotBlank()) {
            val params = fragment.split("&").associate {
                val pair = it.split("=")
                pair.firstOrNull().orEmpty() to pair.getOrNull(1).orEmpty()
            }
            val token = params["access_token"]
            if (!token.isNullOrBlank()) {
                onTokenCaptured(token)
            }
        }
    }
}
