package com.example.reddit_flair_settings.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.example.reddit_flair_settings.R
import com.example.reddit_flair_settings.models.*
import org.json.JSONObject
import java.net.URL

class RedditAPIService constructor(context: Context) {
    private val applicationContext: Context = context
    private val requestHandler: RequestHandler = RequestHandler.getInstance(context)
    private val baseUrl = context.resources.getString(R.string.api_base_url)

    fun getUserIdentity(listener: Response.Listener<JSONObject>, errorListener: Response.ErrorListener) {
        val url = "$baseUrl/api/v1/me"

        requestHandler.sendAuthorizedRequest(applicationContext, Request.Method.GET, url, null,
            { response ->
                listener.onResponse(response)
            }, errorListener)
    }

    fun getUserSubreddits(cb: (x: MutableList<UserSubreddit>) -> Unit, errorListener: Response.ErrorListener) {
        val url = "$baseUrl/subreddits/mine/subscriber"
        requestHandler.sendAuthorizedRequest(applicationContext, Request.Method.GET, url, null,
            { response ->
                val subreddits = response.getJSONObject("data").getJSONArray("children")
                val res: MutableList<UserSubreddit> = mutableListOf()

                for (i in 0 until subreddits.length()) {
                    val subData = subreddits.getJSONObject(i).getJSONObject("data")
                    var richText: MutableList<FlairRichTextComponent>?

                    val richTextComponents = subData.getJSONArray("user_flair_richtext")

                    val flairType: String = subData.getString("user_flair_type")

                    if (flairType == "richtext") {
                        richText = mutableListOf()
                        for (j in 0 until richTextComponents.length()) {
                            val component = richTextComponents.getJSONObject(j)
                            val type = component.getString("e")
                            if (type == "text") {
                                val text = component.getString("t")
                                richText.add(TextComponent(text))
                            } else if (type == "emoji") {
                                val id = component.getString("a")
                                val url = component.getString("u")
                                richText.add(IconComponent(id, url))
                            }
                        }
                    } else {
                        richText = null
                    }

                    val userFlair = UserFlair(
                        subData.getString("user_flair_background_color"),
                        subData.getString("user_flair_text_color"),
                        flairType,
                        richText
                    )

                    val iconImg = subData.getString("icon_img")
                    var iconUrl: URL?
                    if (iconImg == "") {
                        iconUrl = null
                    } else {
                        iconUrl = URL(iconImg)
                    }
                    val subreddit = UserSubreddit(
                        subData.getString("display_name_prefixed"),
                        iconUrl,
                        subData.getBoolean("can_assign_user_flair"),
                        userFlair
                    )

                    res.add(subreddit)
                }

                cb(res)

            }, errorListener)
    }

    // /subreddits/mine/subscriber
    // data.children -> data.

    // user_flair_background_color
    // user_flair_richtext
    // display_name_prefixed ex: r/UBC
    // icon_img

    // user_flair_type ex: richtext, text
    // can_assign_user_flair
    // user_flair_text_color ex: dark, light
    // user_flair_css_class
}