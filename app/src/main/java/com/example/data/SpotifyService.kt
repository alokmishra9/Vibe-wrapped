package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Models for Spotify APIs ---

data class SpotifyUser(
    @Json(name = "display_name") val displayName: String?,
    @Json(name = "images") val images: List<SpotifyImage>?
)

data class SpotifyImage(
    @Json(name = "url") val url: String?
)

data class SpotifyTopArtists(
    @Json(name = "items") val items: List<SpotifyArtist>
)

data class SpotifyArtist(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "genres") val genres: List<String>?,
    @Json(name = "images") val images: List<SpotifyImage>?,
    @Json(name = "popularity") val popularity: Int?
)

data class SpotifyTopTracks(
    @Json(name = "items") val items: List<SpotifyTrack>
)

data class SpotifyTrack(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "artists") val artists: List<SpotifyArtistRef>
)

data class SpotifyArtistRef(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String
)

data class SpotifyAudioFeaturesList(
    @Json(name = "audio_features") val audioFeatures: List<SpotifyAudioFeatures?>
)

data class SpotifyAudioFeatures(
    @Json(name = "id") val id: String,
    @Json(name = "energy") val energy: Float,
    @Json(name = "danceability") val danceability: Float,
    @Json(name = "valence") val valence: Float,
    @Json(name = "acousticness") val acousticness: Float,
    @Json(name = "tempo") val tempo: Float
)

// --- Retrofit Interface ---

interface SpotifyService {
    @GET("v1/me")
    suspend fun getUserProfile(
        @Header("Authorization") authHeader: String
    ): SpotifyUser

    @GET("v1/me/top/artists")
    suspend fun getTopArtists(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 20,
        @Query("time_range") timeRange: String = "medium_term"
    ): SpotifyTopArtists

    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 10,
        @Query("time_range") timeRange: String = "medium_term"
    ): SpotifyTopTracks

    @GET("v1/audio-features")
    suspend fun getAudioFeatures(
        @Header("Authorization") authHeader: String,
        @Query("ids") idsString: String
    ): SpotifyAudioFeaturesList
}

// --- Spotify Client Setup ---

object SpotifyClient {
    private const val SPOTIFY_BASE_URL = "https://api.spotify.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: SpotifyService by lazy {
        Retrofit.Builder()
            .baseUrl(SPOTIFY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SpotifyService::class.java)
    }

    // Default Client ID (users can supply their own inside the app as a secure setting, which is amazing!)
    const val DEFAULT_CLIENT_ID = "be7d363d33df45f3bfdf2cbf5fc1bd0a" // Safe fallback public/client-auth token placeholder
    
    val CLIENT_ID: String
        get() = com.example.BuildConfig.SPOTIFY_CLIENT_ID.ifBlank { DEFAULT_CLIENT_ID }

    val REDIRECT_URI: String
        get() = com.example.BuildConfig.SPOTIFY_REDIRECT_URI.ifBlank { "https://localhost/callback" }

    const val AUTH_SCOPES = "user-read-private user-top-read user-read-recently-played"
}
