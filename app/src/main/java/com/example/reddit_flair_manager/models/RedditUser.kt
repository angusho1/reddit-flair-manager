package com.example.reddit_flair_manager.models

import java.net.URL

class RedditUser(
    val name: String,
    snoovatarURL: String
) {
    val snoovatarURL: URL = URL(snoovatarURL)
}