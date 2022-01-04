package com.example.reddit_flair_settings.models

interface FlairRichTextComponent {

}

class TextComponent(val text: String) : FlairRichTextComponent {

}

class IconComponent(val id: String, val url: String) : FlairRichTextComponent {

}

