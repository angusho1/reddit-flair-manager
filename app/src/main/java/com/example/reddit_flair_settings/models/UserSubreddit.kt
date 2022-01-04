package com.example.reddit_flair_settings.models

import java.net.URL

data class UserSubreddit(
    val name: String, // display_name_prefixed
    val iconUrl: URL?, // icon_img
    val flairsEnabled: Boolean, // can_assign_user_flair
    val flair: UserFlair
)