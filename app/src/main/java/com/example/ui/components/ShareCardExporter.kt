package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.SavedWrapped
import com.example.data.GenreShare
import java.io.File
import java.io.FileOutputStream

object ShareCardExporter {

    fun exportAndShare(context: Context, wrap: SavedWrapped, genres: List<GenreShare>) {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Premium Obsidian/Neon Gradient Background
        val bgPaint = Paint().apply { isAntiAlias = true }
        val bgGrad = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#08090C"), // Extreme deep obsidian
                Color.parseColor("#12141D"), // Dark blue slate
                Color.parseColor("#1C031A")  // Deep magenta glow corner
            ),
            null, Shader.TileMode.CLAMP
        )
        bgPaint.shader = bgGrad
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Add subtle neon accent background bubbles
        val glowPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(200f, BlurMaskFilter.Blur.NORMAL)
        }
        glowPaint.color = Color.parseColor("#1DB954") // Spotify green soft glow
        canvas.drawCircle(width.toFloat(), 0f, 350f, glowPaint)

        glowPaint.color = Color.parseColor("#FF007A") // Cyber pink glow
        canvas.drawCircle(0f, height.toFloat() * 0.7f, 400f, glowPaint)

        glowPaint.color = Color.parseColor("#00F0FF") // Electric blue glow
        canvas.drawCircle(width.toFloat() * 0.8f, height.toFloat() * 0.4f, 250f, glowPaint)

        // Reset blur mask filters
        glowPaint.maskFilter = null

        // 2. Draw Sleek Top Header Banner
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        // Draw Title "VIBE WRAPPED"
        textPaint.textSize = 72f
        canvas.drawText("VIBE WRAPPED", width / 2f, 160f, textPaint)

        // Draw profile tagline
        textPaint.textSize = 32f
        textPaint.color = Color.parseColor("#1DB954")
        canvas.drawText("MUSIC DNA & ASTROLOGY • BY @${wrap.profileName.uppercase()}", width / 2f, 220f, textPaint)

        // Draw thin neon line separating header
        val linePaint = Paint().apply {
            color = Color.parseColor("#33FFFFFF")
            strokeWidth = 3f
        }
        canvas.drawLine(100f, 260f, width - 100f, 260f, linePaint)

        // 3. Draw Listening Archetype Section
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.color = Color.parseColor("#66FFFFFF")
        textPaint.textSize = 28f
        canvas.drawText("YOUR LISTENING ARCHETYPE:", 120f, 330f, textPaint)

        textPaint.color = Color.parseColor("#FF007A")
        textPaint.textSize = 58f
        canvas.drawText(wrap.archetype.uppercase(), 120f, 400f, textPaint)

        // 4. Draw Custom Concentric Genre DNA Arc Chart
        textPaint.color = Color.parseColor("#33FFFFFF")
        canvas.drawLine(120f, 450f, width - 120f, 450f, linePaint)

        textPaint.color = Color.parseColor("#66FFFFFF")
        textPaint.textSize = 28f
        canvas.drawText("GENRE DNA PROFILE:", 120f, 490f, textPaint)

        var startAngle = -90f
        val rectF = RectF(120f, 540f, 420f, 840f)
        val piePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 45f
            strokeCap = Paint.Cap.ROUND
        }

        genres.forEach { genre ->
            val sweepAngle = (genre.percentage / 100f) * 360f
            piePaint.color = Color.parseColor(genre.hexColor)
            canvas.drawArc(rectF, startAngle, sweepAngle, false, piePaint)
            startAngle += sweepAngle
        }

        // Draw Genre Legend
        var legendY = 580f
        genres.take(4).forEach { genre ->
            // Color dot
            val dotPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = Color.parseColor(genre.hexColor)
            }
            canvas.drawCircle(500f, legendY - 12f, 14f, dotPaint)

            // Text
            textPaint.color = Color.WHITE
            textPaint.textSize = 36f
            canvas.drawText(genre.name, 540f, legendY, textPaint)

            // Percentage
            textPaint.color = Color.parseColor(genre.hexColor)
            textPaint.textSize = 34f
            canvas.drawText("${genre.percentage}%", 840f, legendY, textPaint)

            legendY += 75f
        }

        // 5. Draw Mood Spectrum Visualizer
        canvas.drawLine(120f, 900f, width - 120f, 900f, linePaint)

        textPaint.color = Color.parseColor("#66FFFFFF")
        textPaint.textSize = 28f
        canvas.drawText("MOOD SPECTRUM CHARACTERISTICS:", 120f, 950f, textPaint)

        val features = listOf(
            Triple("VIBE ENERGY", wrap.energy, "#00FFCC"),
            Triple("GROOVE RATE", wrap.danceability, "#FF007A"),
            Triple("VALENCE HUE", wrap.valence, "#EEEF20"),
            Triple("ORGANIC DEPTH", wrap.acousticness, "#A855F7")
        )

        var barY = 1000f
        features.forEach { (label, value, hexColor) ->
            textPaint.color = Color.WHITE
            textPaint.textSize = 26f
            canvas.drawText(label, 120f, barY, textPaint)

            textPaint.color = Color.parseColor(hexColor)
            textPaint.textSize = 26f
            canvas.drawText("${(value * 100).toInt()}%", width - 200f, barY, textPaint)

            // Bar background
            val barBgPaint = Paint().apply {
                color = Color.parseColor("#1EFFFFFF")
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(120f, barY + 15f, width - 120f, barY + 30f, 10f, 10f, barBgPaint)

            // Bar fill
            val barFillPaint = Paint().apply {
                color = Color.parseColor(hexColor)
                style = Paint.Style.FILL
            }
            val fillWidth = 120f + (width - 240f) * value
            canvas.drawRoundRect(120f, barY + 15f, fillWidth, barY + 30f, 10f, 10f, barFillPaint)

            barY += 85f
        }

        // 6. Draw Alter Ego Person Card Content (High Visual Highlight Box)
        canvas.drawLine(120f, 1370f, width - 120f, 1370f, linePaint)

        val cardBgPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#151825")
            style = Paint.Style.FILL
        }
        val cardBorderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FF007A")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        val cardRect = RectF(120f, 1400f, width - 120f, 1720f)
        canvas.drawRoundRect(cardRect, 24f, 24f, cardBgPaint)
        canvas.drawRoundRect(cardRect, 24f, 24f, cardBorderPaint)

        // Draw Alter Ego Details inside
        textPaint.color = Color.parseColor("#FF007A")
        textPaint.textSize = 30f
        canvas.drawText("MUSIC ALTER-EGO:", 160f, 1460f, textPaint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 48f
        canvas.drawText(wrap.alterEgoName.uppercase(), 160f, 1520f, textPaint)

        // Draw Alter Ego Description with wrapping
        textPaint.color = Color.parseColor("#CCCCCC")
        textPaint.textSize = 26f
        val wrappedBio = wrap.alterEgoDescription
        val bioPaint = Paint().apply {
            color = Color.parseColor("#DDDDDD")
            textSize = 28f
            isAntiAlias = true
        }

        // Multi-line manual wrapping helper
        val words = wrappedBio.split(" ")
        var lineString = ""
        var lineY = 1580f
        words.forEach { word ->
            val testLine = if (lineString.isEmpty()) word else "$lineString $word"
            val widthOfTest = bioPaint.measureText(testLine)
            if (widthOfTest > (width - 320f)) {
                canvas.drawText(lineString, 160f, lineY, textPaint)
                lineString = word
                lineY += 40f
            } else {
                lineString = testLine
            }
        }
        if (lineString.isNotEmpty()) {
            canvas.drawText(lineString, 160f, lineY, textPaint)
        }

        // 7. Footer branding
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.parseColor("#88FFFFFF")
        textPaint.textSize = 26f
        canvas.drawText("CREATE YOUR AUDIO DNA • ON VIBE WRAPPED APP 🎵", width / 2f, 1830f, textPaint)

        // Save file to cache and initiate sharing
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "v_wrapped_share_${wrap.id}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // Get standard FileProvider Uri
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Create share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Vibe Wrapped DNA"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
