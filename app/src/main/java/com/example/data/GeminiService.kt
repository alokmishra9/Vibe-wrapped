package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

// --- Gemini Request / Response Models ---

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null,
    val responseSchema: Map<String, Any>? = null
)

// The structured response we want from Gemini
data class tasteAnalysisJson(
    val alterEgoName: String,
    val alterEgoBio: String,
    val roast: String,
    val behaviorRoasts: List<String>
)

object GeminiClient {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Dynamically generates a humorous music personality profile.
     * If the API Key is empty, we fall back to our premium prebuilt local intelligence.
     */
    suspend fun generateTasteProfile(
        profileName: String,
        archetype: java.lang.String, // use standard String in java/kotlin
        genres: List<String>,
        artists: List<String>,
        energy: Float,
        danceability: Float,
        valence: Float,
        acousticness: Float
    ): tasteAnalysisJson = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Local fallback
            return@withContext getLocalTasteFallback(archetype.toString(), genres, artists)
        }

        val prompt = """
            Analyze this Spotify listening profile for user '$profileName'.
            Archetype: $archetype
            Genres: ${genres.joinToString(", ")}
            Favorite Artists: ${artists.joinToString(", ")}
            Metrics: Energy=${(energy * 100).toInt()}%, Danceability=${(danceability * 100).toInt()}%, Valence=${(valence * 100).toInt()}%, Acousticness=${(acousticness * 100).toInt()}%
            
            Based on this, return a JSON response containing:
            1. 'alterEgoName': A hilarious, short (2-3 words), hype title like 'Symphonic Goth-Pop Overlord' or '2AM Espresso Poet'.
            2. 'alterEgoBio': A beautiful 1-sentence description.
            3. 'roast': A brutal but light-hearted and funny roast about their listening habits (2 sentences).
            4. 'behaviorRoasts': A JSON array of 3 funny, hyper-specific bullet point observations. Examples: "Owns 4 pairs of sunglasses for dark indoor gigs", "Has cried to a synth solo at least twice", "Tells everyone they discovered this artist before the TikTok trend".
            
            Return ONLY a valid JSON object matching the requested schema. No other text.
        """.trimIndent()

        val systemPrompt = "You are a professional Spotify Wrapped music critic. You are witty, deeply knowledgeable about subcultures, brutally funny but lovable, and write in a trendy style suited for Instagram/TikTok audiences."

        val requestPayload = mapOf(
            "contents" to listOf(
                mapOf("parts" to listOf(mapOf("text" to prompt)))
            ),
            "systemInstruction" to mapOf(
                "parts" to listOf(mapOf("text" to systemPrompt))
            ),
            "generationConfig" to mapOf(
                "temperature" to 0.85f,
                "responseMimeType" to "application/json"
            )
        )

        val jsonString = moshi.adapter(Map::class.java).toJson(requestPayload)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP error code: ${response.code}")
                }
                val body = response.body?.string() ?: throw IOException("Empty body")
                
                // Parse the response candidates
                val parsedRoot = moshi.adapter(Map::class.java).fromJson(body)
                val candidates = parsedRoot?.get("candidates") as? List<*>
                val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
                val content = firstCandidate?.get("content") as? Map<*, *>
                val parts = content?.get("parts") as? List<*>
                val firstPart = parts?.firstOrNull() as? Map<*, *>
                val generatedJsonText = firstPart?.get("text") as? String
                    ?: throw IOException("No text field in response")

                // Parse the inner JSON matching tasteAnalysisJson
                moshi.adapter(tasteAnalysisJson::class.java).fromJson(generatedJsonText)
                    ?: throw IOException("Failed to parse inner JSON")
            }
        } catch (e: Exception) {
            // Fallback on failure
            getLocalTasteFallback(archetype.toString(), genres, artists)
        }
    }

    private fun getLocalTasteFallback(archetype: String, genres: List<String>, artists: List<String>): tasteAnalysisJson {
        val topArtist = artists.firstOrNull() ?: "mysterious artists"
        val topGenre = genres.firstOrNull() ?: "ambient silence"

        return when {
            archetype.contains("Night Owl", true) -> tasteAnalysisJson(
                alterEgoName = "Vampiric Soundscaper 🦇",
                alterEgoBio = "You exist entirely in blue-light filters, high fidelity bass hums, and coffee-fueled twilight.",
                roast = "Your sleep schedule is so inverted you've forgotten what a sunbeam looks like. You play $topGenre tracks as if they are ambient prescription medicine rather than sound files.",
                behaviorRoasts = listOf(
                    "You think $topArtist is your soulmate based purely on their instrumentals.",
                    "Has 9 different playlists named after different intensities of midnight rain.",
                    "Regularly tells people 'it sounds different in high-impedance headphones.'"
                )
            )
            archetype.contains("Devotee", true) -> tasteAnalysisJson(
                alterEgoName = "Main Character Monomaniac 🎧",
                alterEgoBio = "A passionate laser-focused listener who holds eye contact with songs for too long.",
                roast = "When you like a song, you play it until the actual audio file is structurally compromised. Your Spotify profile is basically a single track on life-support.",
                behaviorRoasts = listOf(
                    "You know the exact millisecond the bridge starts to the point where your heart rate spikes.",
                    "Actually believes $topArtist wrote that track specifically after viewing your private story.",
                    "Refuses to hand over the AUX cord because of a 'very delicate narrative build-up' only you can hear."
                )
            )
            archetype.contains("Euphoric", true) || archetype.contains("Dreamer", true) -> tasteAnalysisJson(
                alterEgoName = "Neon Sparkle Grenade ⚡",
                alterEgoBio = "Living in an high-speed, sugary, bubblegum-pink simulation where everything is set to 160 BPM.",
                roast = "Your music tastes like carbonated candy that explodes. You listen to $topArtist because quiet acoustic instruments make you break out in hives.",
                behaviorRoasts = listOf(
                    "Considers 140 BPM to be a 'chill bedtime ballad.'",
                    "Needs sparkle, high pitch synth filters, and neon drops just to complete basic household chores.",
                    "Has a short attention span that can only be sustained by instant, colorful drops."
                )
            )
            archetype.contains("Traveler", true) -> tasteAnalysisJson(
                alterEgoName = "Vntg Time-Warp Historian 🕰️",
                alterEgoBio = "A retro purist dwelling in dynamic nostalgia for decades you didn't even get to experience.",
                roast = "Your ears are permanently stuck in vintage tape hiss. You probably tell people you are 'such an old soul' while playing vinyl-rips on a streaming app.",
                behaviorRoasts = listOf(
                    "Actually thinks that music completely died in 1999 and refuses to hear otherwise.",
                    "Has argued with someone at a dinner table about whether tape compression is superior.",
                    "This is vintage aesthetic, but your Spotify stats reveal you listen to remastered stadium hits."
                )
            )
            archetype.contains("philosopher", true) -> tasteAnalysisJson(
                alterEgoName = "Chamber-Indie Soliloquist 🕯️",
                alterEgoBio = "Staring poetically out of rainy windows while indie folk pluckings tear your soul into fine pieces.",
                roast = "You don't listen to music to be happy; you listen to music to simulate rich, cinematic existential despair. If a song has electric guitar distortion, you feel physically attacked.",
                behaviorRoasts = listOf(
                    "Stares wistfully at the transit floor while pretending a soft string solo is your personal cinematic montage.",
                    "Can't go to a coffee shop without writing a half-poem in your Notes app.",
                    "Thinks $topArtist is talking directly to you through their vague acoustic verses."
                )
            )
            else -> tasteAnalysisJson(
                alterEgoName = "Chaotic Alchemy Generalist 🔮",
                alterEgoBio = "Blending mutually exclusive musical universes together because genre labels are for the uninspired.",
                roast = "Your taste profile looks like a collision of 12 distinct subcultures. You jump from brutal noise to sparkling dance and expect everyone's nervous system to handle it.",
                behaviorRoasts = listOf(
                    "Has a playlist that can induce whiplash in any passenger riding in your car.",
                    "Refuses to define your taste because you are 'multi-dimensional.'",
                    "Your top artists look like they would actively avoid each other at a festival."
                )
            )
        }
    }
}
