package com.example.reddit_flair_manager.models

import android.graphics.Color

const val FLAIR_TYPE_TEXT = 0
const val FLAIR_TYPE_RICH_TEXT = 1

class UserFlair(
    backgroundColor: String,
    textColor: String,
    type: String,
    val richText: List<FlairRichTextComponent>?
) {
    val backgroundColor: Int // user_flair_background_color
    val textColor: Int // user_flair_text_color
    val type: Int // user_flair_type

    init {
        this.backgroundColor = getColor(backgroundColor)
        this.textColor = getColor(textColor)
        this.type = getFlairType(type)
    }

    private fun getColor(colorString: String): Int {
        return if (colorString == "") {
            Color.TRANSPARENT
        } else if (colorString.substring(0, 1) == "#") {
            Color.parseColor(colorString)
        } else if (colorString == "light") {
            Color.WHITE
        } else if (colorString == "dark") {
            Color.BLACK
        } else {
            Color.TRANSPARENT
        }
    }

    private fun getFlairType(type: String): Int {
        return when (type) {
            "text" -> {
                FLAIR_TYPE_TEXT
            }
            "richtext" -> {
                FLAIR_TYPE_RICH_TEXT
            }
            else -> {
                FLAIR_TYPE_TEXT
            }
        }
    }
}