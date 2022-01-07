package com.example.reddit_flair_settings.models

const val VIEW_TYPE_TEXT = 0
const val VIEW_TYPE_IMAGE = 1

interface FlairRichTextComponent {
    fun getViewType(): Int
}

class TextComponent(val text: String) : FlairRichTextComponent {
    override fun getViewType(): Int {
        return VIEW_TYPE_TEXT
    }

}

class IconComponent(val id: String, val url: String) : FlairRichTextComponent {
    override fun getViewType(): Int {
        return VIEW_TYPE_IMAGE
    }

}

