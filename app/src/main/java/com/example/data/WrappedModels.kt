package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_wrapped")
data class SavedWrapped(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileName: String,
    val archetype: String,
    val genresJson: String, // format: "Pop:45,Rock:25,Lofi:15..."
    val energy: Float,
    val danceability: Float,
    val valence: Float,
    val acousticness: Float,
    val alterEgoName: String,
    val alterEgoDescription: String,
    val roast: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class GenreShare(
    val name: String,
    val percentage: Int,
    val hexColor: String
)

data class ListeningArchetype(
    val name: String,
    val title: String,
    val tagline: String,
    val description: String,
    val iconName: String, // descriptive icon reference
    val moodMatch: String
) {
    companion object {
        val ALL = listOf(
            ListeningArchetype(
                name = "The Sonic Nomad",
                title = "The Sonic Nomad 🌌",
                tagline = "Your ears gather passport stamps.",
                description = "You reject radio playlists and mainstream feeds. You seek out rare, global, avant-garde vibrations. You probably tell people 'you've probably never heard of them' but in a sweet, well-meaning way.",
                iconName = "explore",
                moodMatch = "High Variety, Low Popularity"
            ),
            ListeningArchetype(
                name = "The Devotee",
                title = "The Devotee 💖",
                tagline = "Loyalty is your middle name.",
                description = "When you discover an artist or song, you play it 867 times in 3 days until it's vacuum-sealed in your skull. Your friends are concerned about your obsessive loops, but you call it 'true devotion.'",
                iconName = "repeat",
                moodMatch = "Extreme Hyperfocus, High Repeat Rate"
            ),
            ListeningArchetype(
                name = "The Night Owl",
                title = "The Night Owl 🦉",
                tagline = "Midnight's favorite companion.",
                description = "Your peak listening hours are 2:00 AM. You feed on twilight dark, moody techno, deep house, Lofi, or ambient instrumentals. Your solar cycle is inverted and your coffee intake is alarming.",
                iconName = "dark_mode",
                moodMatch = "Late Night, Low Energy, High Introspection"
            ),
            ListeningArchetype(
                name = "The Alchemist",
                title = "The Alchemist 🧪",
                tagline = "The master blender.",
                description = "You curate highly specific playlists and enjoy massive genre transitions (like moving from ultra-brutal Swedish Death Metal directly to Chopin's Nocturnes). You treat music like a chemical reaction.",
                iconName = "opacity",
                moodMatch = "High Playlists, Extreme Contrasts"
            ),
            ListeningArchetype(
                name = "The Time Traveler",
                title = "The Time Traveler 🕰️",
                tagline = "Living in the wrong decade.",
                description = "Your listening DNA is packed with hits from decades you weren't even alive to witness. Nostalgia is your primary fuel, and you're absolutely convinced that 'music today just isn't the same.'",
                iconName = "history",
                moodMatch = "High Average Release Age"
            ),
            ListeningArchetype(
                name = "The Vibe Curator",
                title = "The Vibe Curator 🌡️",
                tagline = "Tailoring of soundscapes.",
                description = "Every rainstorm, coffee run, shower, and sad walk requires a distinct 4-hour soundtrack. Music isn't background noise; it is the fundamental atmospheric material of your daily survival.",
                iconName = "palette",
                moodMatch = "High Valence Shifts, Precise Categories"
            ),
            ListeningArchetype(
                name = "The Euphoric Dreamer",
                title = "The Euphoric Dreamer ✨",
                tagline = "Hyper-charged, sweet-toothed melody lover.",
                description = "You live in a bright, neon bubble of hyperpop, fast-paced techno, synthwave, or sugary pop. You need high bpm, sparkling synthesizers, and pure dopamine delivered straight to your sensory cortex.",
                iconName = "bolt",
                moodMatch = "High Tempo, High Energy, High Valence"
            ),
            ListeningArchetype(
                name = "The deep philosopher",
                title = "The Deep Philosopher 🧭",
                tagline = "Intricate lyrics over empty beats.",
                description = "Drawn to poetic, soft indie folk, classical chamber music, and sweeping ambient symphonies. You read lyrics line-by-line and stare out of bus window pans imagining tragic cinematic scenarios.",
                iconName = "menu_book",
                moodMatch = "High Acousticness, High Lyrics, Low Tempo"
            )
        )

        fun classify(genres: List<String>, energy: Float, danceability: Float, valence: Float, acousticness: Float): ListeningArchetype {
            // Simple heuristic mapping
            return when {
                acousticness > 0.65f -> ALL.first { it.name == "The deep philosopher" }
                energy > 0.75f && valence > 0.65f -> ALL.first { it.name == "The Euphoric Dreamer" }
                genres.any { it.contains("lofi", true) || it.contains("ambient", true) } && energy < 0.45f -> ALL.first { it.name == "The Night Owl" }
                genres.any { it.contains("classic", true) || it.contains("old", true) || it.contains("retro", true) || it.contains("vintage", true) } -> ALL.first { it.name == "The Time Traveler" }
                genres.size > 8 && (energy > 0.6f && energy < 0.8f) -> ALL.first { it.name == "The Sonic Nomad" }
                danceability > 0.75f && energy > 0.7f -> ALL.first { it.name == "The Devotee" } // energetic loopers
                genres.any { it.contains("indie", true) || it.contains("folk", true) } && valence < 0.5f -> ALL.first { it.name == "The Vibe Curator" }
                else -> ALL.first { it.name == "The Alchemist" }
            }
        }
    }
}
