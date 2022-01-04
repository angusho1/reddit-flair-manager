package com.example.reddit_flair_settings.models

data class UserFlair (
    val backgroundColor: String, // user_flair_background_color
    val textColor: String, // user_flair_text_color
    val type: String, // user_flair_type
    val richText: List<FlairRichTextComponent>?
)