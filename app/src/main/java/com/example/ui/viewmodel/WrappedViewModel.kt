package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface WrappedUiState {
    object Welcome : WrappedUiState
    data class Loading(val message: String) : WrappedUiState
    data class StorySlides(val wrap: SavedWrapped, val currentSlideIndex: Int) : WrappedUiState
    object SavedWrappersList : WrappedUiState
}

class WrappedViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WrappedRepository(db.savedWrappedDao())

    private val _uiState = MutableStateFlow<WrappedUiState>(WrappedUiState.Welcome)
    val uiState: StateFlow<WrappedUiState> = _uiState.asStateFlow()

    private val _isWebViewOpen = MutableStateFlow(false)
    val isWebViewOpen: StateFlow<Boolean> = _isWebViewOpen.asStateFlow()

    private val _spotifyAuthUrl = MutableStateFlow("")
    val spotifyAuthUrl: StateFlow<String> = _spotifyAuthUrl.asStateFlow()

    // Observe saved summaries from Room
    val savedWraps: StateFlow<List<SavedWrapped>> = repository.allWraps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateToWelcome() {
        _uiState.value = WrappedUiState.Welcome
    }

    fun navigateToPastWraps() {
        _uiState.value = WrappedUiState.SavedWrappersList
    }

    fun deleteWrap(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllWraps() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun launchSpotifyLogin(customClientId: String = "") {
        val clientId = if (customClientId.isNotBlank()) customClientId else SpotifyClient.CLIENT_ID
        // Using response_type=token (Implicit Grant) is client-side only & highly bulletproof for WebViews!
        val url = "https://accounts.spotify.com/authorize?" +
                "client_id=$clientId" +
                "&response_type=token" +
                "&redirect_uri=${SpotifyClient.REDIRECT_URI}" +
                "&scope=${SpotifyClient.AUTH_SCOPES.replace(" ", "%20")}" +
                "&show_dialog=true"
        _spotifyAuthUrl.value = url
        _isWebViewOpen.value = true
    }

    fun closeSpotifyWebView() {
        _isWebViewOpen.value = false
    }

    fun handleSpotifyAccessToken(token: String) {
        _isWebViewOpen.value = false
        _uiState.value = WrappedUiState.Loading("Synchronizing with Spotify data streams...")

        viewModelScope.launch {
            try {
                val header = "Bearer $token"
                
                // 1. Query Spotify User Profile
                _uiState.value = WrappedUiState.Loading("Querying user profile info...")
                val spotifyUser = SpotifyClient.service.getUserProfile(header)
                val displayName = spotifyUser.displayName ?: "Music Fan"

                // 2. Fetch Top Artists
                _uiState.value = WrappedUiState.Loading("Analyzing top genres & artists...")
                val topArtistsResponse = SpotifyClient.service.getTopArtists(header, limit = 20)
                
                // Extract genres and build frequencies
                val genreCounts = mutableMapOf<String, Int>()
                topArtistsResponse.items.forEach { artist ->
                    artist.genres?.forEach { genre ->
                        genreCounts[genre] = (genreCounts[genre] ?: 0) + 1
                    }
                }
                
                // 3. Fetch Top Tracks
                _uiState.value = WrappedUiState.Loading("Retrieving audio features & sound characteristics...")
                val topTracksResponse = SpotifyClient.service.getTopTracks(header, limit = 10)
                
                // Get features for these track IDs
                val trackIds = topTracksResponse.items.map { it.id }.joinToString(",")
                
                var avgEnergy = 0.5f
                var avgDance = 0.5f
                var avgValence = 0.5f
                var avgAcoustic = 0.3f
                
                if (trackIds.isNotEmpty()) {
                    val featuresResponse = SpotifyClient.service.getAudioFeatures(header, trackIds)
                    val list = featuresResponse.audioFeatures.filterNotNull()
                    if (list.isNotEmpty()) {
                        avgEnergy = list.map { it.energy }.average().toFloat()
                        avgDance = list.map { it.danceability }.average().toFloat()
                        avgValence = list.map { it.valence }.average().toFloat()
                        avgAcoustic = list.map { it.acousticness }.average().toFloat()
                    }
                }

                // Format Top Genres (e.g. Pop:45,Rock:25)
                val totalGenres = genreCounts.values.sum()
                val topGenres = genreCounts.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .map { entry ->
                        val pct = if (totalGenres > 0) ((entry.key.capitalizeAscii() to (entry.value * 100 / totalGenres))) else (entry.key.capitalizeAscii() to 20)
                        pct
                    }
                
                // Normalize genres string
                val genresString = topGenres.joinToString(",") { "${it.first}:${it.second}" }
                
                // Extract Top Artists / Track strings for analysis
                val artistNames = topArtistsResponse.items.take(3).map { it.name }
                val genreNamesOnly = topGenres.map { it.first }

                // 4. Classify Archetype
                val archetype = ListeningArchetype.classify(
                    genres = genreNamesOnly,
                    energy = avgEnergy,
                    danceability = avgDance,
                    valence = avgValence,
                    acousticness = avgAcoustic
                )

                // 5. Generate with Gemini AI / Fallback
                _uiState.value = WrappedUiState.Loading("Analyzing coordinates and crafting your roast...")
                val tasteProfile = GeminiClient.generateTasteProfile(
                    profileName = displayName,
                    archetype = java.lang.String(archetype.name),
                    genres = genreNamesOnly,
                    artists = artistNames,
                    energy = avgEnergy,
                    danceability = avgDance,
                    valence = avgValence,
                    acousticness = avgAcoustic
                )

                // 6. Build the Wrapped Result
                val newWrap = SavedWrapped(
                    profileName = displayName,
                    archetype = archetype.name,
                    genresJson = genresString,
                    energy = avgEnergy,
                    danceability = avgDance,
                    valence = avgValence,
                    acousticness = avgAcoustic,
                    alterEgoName = tasteProfile.alterEgoName,
                    alterEgoDescription = tasteProfile.alterEgoBio,
                    roast = tasteProfile.roast + "\n\n• " + tasteProfile.behaviorRoasts.joinToString("\n• ")
                )

                // Save to historical DB
                repository.insert(newWrap)
                
                // Start Story Slideshow
                _uiState.value = WrappedUiState.StorySlides(newWrap, 0)

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = WrappedUiState.Loading("Error synchronization: ${e.message}. Please verify your Spotify Developer setup or use Instant Vibe Analyzer.")
                // Wait 4 seconds then pop back to login
                kotlinx.coroutines.delay(4000)
                _uiState.value = WrappedUiState.Welcome
            }
        }
    }

    fun loadCustomVibe(
        name: String,
        artistsInput: String,
        genresInput: String,
        vibeScale: String // "Chill", "Banger", "Moody", "Sunset", "Goth"
    ) {
        _uiState.value = WrappedUiState.Loading("Calculating your custom vibe spectrum...")
        viewModelScope.launch {
            try {
                val cleanName = if (name.isNotBlank()) name else "Astral Listener"
                val artists = artistsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val genres = genresInput.split(",").map { it.trim().capitalizeAscii() }.filter { it.isNotBlank() }

                val finalArtists = if (artists.isNotEmpty()) artists else listOf("The Beatles", "Daft Punk", "Billie Eilish")
                val finalGenres = if (genres.isNotEmpty()) genres else listOf("Indie Pop", "Synthwave", "Jazz")

                // Map slider selectors to characteristics
                var energy = 0.5f
                var dance = 0.5f
                var valence = 0.5f
                var acoustic = 0.3f

                when (vibeScale) {
                    "Chill" -> { energy = 0.25f; dance = 0.3f; valence = 0.55f; acoustic = 0.65f }
                    "Banger" -> { energy = 0.85f; dance = 0.90f; valence = 0.75f; acoustic = 0.05f }
                    "Moody" -> { energy = 0.35f; dance = 0.4f; valence = 0.20f; acoustic = 0.5f }
                    "Sunset" -> { energy = 0.6f; dance = 0.65f; valence = 0.8f; acoustic = 0.35f }
                    "Goth" -> { energy = 0.75f; dance = 0.5f; valence = 0.15f; acoustic = 0.1f }
                }

                // Build a custom genre percentages string
                val totalSlices = finalGenres.size
                val genresString = finalGenres.mapIndexed { idx, genre ->
                    val share = if (idx == 0) 45 else if (idx == 1) 30 else 25 / (totalSlices - 2).coerceAtLeast(1)
                    "$genre:$share"
                }.joinToString(",")

                val archetype = ListeningArchetype.classify(
                    genres = finalGenres,
                    energy = energy,
                    danceability = dance,
                    valence = valence,
                    acousticness = acoustic
                )

                _uiState.value = WrappedUiState.Loading("Injecting AI commentary of your customized profile...")
                val tasteProfile = GeminiClient.generateTasteProfile(
                    profileName = cleanName,
                    archetype = java.lang.String(archetype.name),
                    genres = finalGenres,
                    artists = finalArtists,
                    energy = energy,
                    danceability = dance,
                    valence = valence,
                    acousticness = acoustic
                )

                val newWrap = SavedWrapped(
                    profileName = cleanName,
                    archetype = archetype.name,
                    genresJson = genresString,
                    energy = energy,
                    danceability = dance,
                    valence = valence,
                    acousticness = acoustic,
                    alterEgoName = tasteProfile.alterEgoName,
                    alterEgoDescription = tasteProfile.alterEgoBio,
                    roast = tasteProfile.roast + "\n\n• " + tasteProfile.behaviorRoasts.joinToString("\n• ")
                )

                repository.insert(newWrap)
                _uiState.value = WrappedUiState.StorySlides(newWrap, 0)
            } catch (e: Exception) {
                _uiState.value = WrappedUiState.Loading("Error building custom vibe: ${e.message}")
                kotlinx.coroutines.delay(3000)
                _uiState.value = WrappedUiState.Welcome
            }
        }
    }

    fun loadPresetDemoProfile(type: String) {
        val name = when (type) {
            "techno" -> "Rave Shaman"
            "lofi" -> "Corner Desk Poet"
            "metal" -> "Riff Alchemist"
            else -> "Aero Dreamer"
        }

        val artists = when (type) {
            "techno" -> listOf("Charlotte de Witte", "Aphex Twin", "Amelie Lens")
            "lofi" -> listOf("ChilledCow", "Idealism", "jinsang")
            "metal" -> listOf("Opeth", "Gojira", "Mastodon")
            else -> listOf("Chopin", "Ludovico Einaudi", "Max Richter")
        }

        val genres = when (type) {
            "techno" -> listOf("Dark Techno", "Ambient Synth", "Acid House")
            "lofi" -> listOf("Lofi Study Beats", "Chillhop", "Jazz Hop")
            "metal" -> listOf("Progressive Metal", "Melodic Death Metal", "Classical Orchestration")
            else -> listOf("Modern Classical", "Chamber Piano", "Ethereal Soundscapes")
        }

        loadCustomVibe(
            name = name,
            artistsInput = artists.joinToString(","),
            genresInput = genres.joinToString(","),
            vibeScale = when (type) {
                "techno" -> "Banger"
                "lofi" -> "Chill"
                "metal" -> "Goth"
                else -> "Sunset"
            }
        )
    }

    fun launchSavedWrapInSlideshow(wrap: SavedWrapped) {
        _uiState.value = WrappedUiState.StorySlides(wrap, 0)
    }

    fun nextSlide(wrap: SavedWrapped, currentIndex: Int) {
        if (currentIndex < 4) {
            _uiState.value = WrappedUiState.StorySlides(wrap, currentIndex + 1)
        } else {
            _uiState.value = WrappedUiState.Welcome // Finished, go back to main menu!
        }
    }

    fun prevSlide(wrap: SavedWrapped, currentIndex: Int) {
        if (currentIndex > 0) {
            _uiState.value = WrappedUiState.StorySlides(wrap, currentIndex - 1)
        } else {
            _uiState.value = WrappedUiState.Welcome
        }
    }

    private fun String.capitalizeAscii(): String =
        this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
