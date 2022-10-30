package com.example.reddit_flair_manager.utils

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

fun getReadableTextColor(originalTextColor: Int, backgroundColor: Int): Int {
    if (backgroundColor == Color.TRANSPARENT) return originalTextColor

    val contrast = ColorUtils.calculateContrast(originalTextColor, backgroundColor)
    val bgLuminance = Color.luminance(backgroundColor)
    return if (contrast > 1.5f) {
        originalTextColor
    } else if (bgLuminance.roundToInt() == 0) { // dark
        Color.WHITE
    } else { // light
        Color.BLACK
    }
}